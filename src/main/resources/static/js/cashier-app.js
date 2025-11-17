class CashierApp {
    constructor() {
        this.currentCashier = null;
        this.csrfToken = null;
        this.csrfHeader = null;
        this.init();
    }

    init() {
        this.loadCsrfToken();
        this.setupEventListeners();
        this.setupDashboardEventListeners();
        this.setupOrderModalListener();
        this.loadDashboardData();
        this.loadOrdersPage();
        this.setupReportsPage();
        this.setupRealTimeUpdates();
        this.availableMenus = [];
        this.orderItemCounter = 0;
    }

    setupOrderModalListener() {
        const newOrderModal = document.getElementById('newOrderModal');
        if (newOrderModal) {
            newOrderModal.addEventListener('show.bs.modal', async () => {
                // Load menus on modal open
                if (this.availableMenus.length === 0) {
                    await this.loadAvailableMenus();
                }
            });
        }

        // Setup payment modal listeners
        this.setupPaymentModalListeners();
    }

    setupPaymentModalListeners() {
        const paymentMethodSelect = document.getElementById('paymentMethod');
        const cashAmountInput = document.getElementById('cashAmount');
        const processPaymentBtn = document.getElementById('processPaymentBtn');

        if (paymentMethodSelect) {
            paymentMethodSelect.addEventListener('change', () => {
                this.togglePaymentSections(paymentMethodSelect.value);
            });
        }

        if (cashAmountInput) {
            cashAmountInput.addEventListener('input', () => {
                this.calculateChange();
            });
        }

        if (processPaymentBtn) {
            processPaymentBtn.addEventListener('click', () => {
                this.processPayment();
            });
        }
    }

    togglePaymentSections(paymentMethod) {
        const cashSection = document.getElementById('cashPaymentSection');
        const qrSection = document.getElementById('qrPaymentSection');

        if (paymentMethod === 'CASH') {
            cashSection.style.display = 'block';
            qrSection.style.display = 'none';
        } else if (paymentMethod === 'QR_CODE') {
            cashSection.style.display = 'none';
            qrSection.style.display = 'block';
        }
    }

    calculateChange() {
        const totalElement = document.getElementById('paymentTotal');
        const cashAmountInput = document.getElementById('cashAmount');
        const changeElement = document.getElementById('changeAmount');

        if (!totalElement || !cashAmountInput || !changeElement) return;

        const total = parseFloat(totalElement.dataset.total || 0);
        const cashAmount = parseFloat(cashAmountInput.value || 0);
        const change = cashAmount - total;

        if (change >= 0) {
            changeElement.value = `Rp ${change.toLocaleString('id-ID')}`;
            changeElement.classList.remove('text-danger');
            changeElement.classList.add('text-success');
        } else {
            changeElement.value = `Kurang Rp ${Math.abs(change).toLocaleString('id-ID')}`;
            changeElement.classList.remove('text-success');
            changeElement.classList.add('text-danger');
        }
    }

    loadCsrfToken() {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');

        if (tokenMeta && headerMeta) {
            this.csrfToken = tokenMeta.getAttribute('content');
            this.csrfHeader = headerMeta.getAttribute('content');
        }
    }

    getCsrfHeaders() {
        if (this.csrfToken && this.csrfHeader) {
            return {
                [this.csrfHeader]: this.csrfToken
            };
        }
        return {};
    }

    setupEventListeners() {
        // Refresh buttons
        document.querySelectorAll('[data-action="refresh"]').forEach(btn => {
            btn.addEventListener('click', () => this.refreshData());
        });

        // Order status updates
        document.addEventListener('click', (e) => {
            if (e.target.closest('[data-order-action]')) {
                const button = e.target.closest('[data-order-action]');
                const action = button.dataset.orderAction;
                const orderId = button.dataset.orderId;
                this.handleOrderAction(orderId, action);
            }
        });

        // Payment processing
        document.addEventListener('click', (e) => {
            if (e.target.closest('[data-payment-action]')) {
                const button = e.target.closest('[data-payment-action]');
                const action = button.dataset.paymentAction;
                const orderNumber = button.dataset.orderNumber;
                this.handlePaymentAction(orderNumber, action);
            }
        });
    }

    async loadDashboardData() {
        try {
            const response = await fetch('/cashier/api/dashboard/stats');
            const data = await response.json();

            if (data.success) {
                this.updateDashboard(data.data);
            }
        } catch (error) {
            console.error('Error loading dashboard data:', error);
            this.showToast('Failed to load dashboard data', 'error');
        }
    }

    updateDashboard(stats) {
        /// Update revenue
        const revenueElement = document.getElementById('todayRevenue');
        if (revenueElement) {
            revenueElement.textContent = `Rp ${stats.todayRevenue?.toLocaleString('id-ID') || '0'}`;
        }

        // Update order counts
        const todayOrdersElement = document.getElementById('todayOrders');
        if (todayOrdersElement) {
            todayOrdersElement.textContent = stats.todayOrders || '0';
        }

        const pendingOrdersElement = document.getElementById('pendingOrders');
        if (pendingOrdersElement) {
            pendingOrdersElement.textContent = stats.pendingOrders || '0';
        }

        const availableMenusElement = document.getElementById('availableMenus');
        if (availableMenusElement) {
            availableMenusElement.textContent = stats.availableMenus || '0';
        }

        // Update recent orders table
        this.updateRecentOrders(stats.recentOrders);

        // Update last update time
        const lastUpdateElement = document.getElementById('lastUpdate');
        if (lastUpdateElement) {
            lastUpdateElement.textContent = new Date().toLocaleTimeString('id-ID');
        }
    }

    updateRecentOrders(orders) {
        const tbody = document.getElementById('recentOrdersBody');
        if (!tbody) return;

        if (!orders || orders.length === 0) {
            tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-muted py-3">
                    Tidak ada pesanan terbaru
                </td>
            </tr>
        `;
            return;
        }

        let html = '';
        orders.forEach(order => {
            html += `
            <tr>
                <td>${order.orderNumber}</td>
                <td>${order.customerName || 'Walk-in Customer'}</td>
                <td>Rp ${order.total?.toLocaleString('id-ID') || '0'}</td>
                <td>
                    <span class="badge bg-${this.getStatusColor(order.status)}">
                        ${order.status}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" 
                            onclick="cashierApp.viewOrder('${order.orderNumber}')">
                        <i class="fas fa-eye"></i>
                    </button>
                </td>
            </tr>
        `;
        });

        tbody.innerHTML = html;
    }

    // Add refresh button handler
    setupDashboardEventListeners() {
        const refreshButton = document.querySelector('[onclick="refreshDashboard()"]');
        if (refreshButton) {
            refreshButton.removeAttribute('onclick');
            refreshButton.addEventListener('click', () => this.refreshDashboard());
        }
    }

    refreshDashboard() {
        this.loadDashboardData();
        this.showToast('Dashboard diperbarui', 'info');
    }

    getStatusColor(status) {
        const statusColors = {
            'PENDING': 'warning',
            'CONFIRMED': 'info',
            'PREPARING': 'primary',
            'READY': 'success',
            'COMPLETED': 'secondary',
            'CANCELLED': 'danger'
        };
        return statusColors[status] || 'secondary';
    }

    formatDateTime(dateString) {
        const date = new Date(dateString);
        const dateOptions = { year: 'numeric', month: '2-digit', day: '2-digit' };
        const timeOptions = { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false };

        const datePart = date.toLocaleDateString('id-ID', dateOptions);
        const timePart = date.toLocaleTimeString('id-ID', timeOptions);

        return `${datePart}, ${timePart}`;
    }

    async handleOrderAction(orderId, action) {
        try {
            let url = '';
            let method = 'PUT';

            switch (action) {
                case 'confirm':
                    url = `/cashier/api/orders/${orderId}/status?status=CONFIRMED`;
                    break;
                case 'preparing':
                    url = `/cashier/api/orders/${orderId}/status?status=PREPARING`;
                    break;
                case 'ready':
                    url = `/cashier/api/orders/${orderId}/status?status=READY`;
                    break;
                case 'complete':
                    url = `/cashier/api/orders/${orderId}/status?status=COMPLETED`;
                    break;
                case 'cancel':
                    url = `/cashier/api/orders/${orderId}/status?status=CANCELLED`;
                    break;
                default:
                    throw new Error('Unknown action');
            }

            const response = await fetch(url, {
                method: method,
                headers: {
                    ...this.getCsrfHeaders()
                }
            });
            const data = await response.json();

            if (data.success) {
                this.showToast(`Pesanan berhasil diupdate!`, 'success');

                // Refresh data based on current page
                if (document.getElementById('ordersTableBody')) {
                    this.loadOrdersPage(); // Refresh orders
                }
                if (document.getElementById('recentOrdersBody')) {
                    this.loadDashboardData(); // Refresh dashboard
                }

                
                if (window.webSocketClient) {
                    window.webSocketClient.sendOrderUpdate(data.data);
                }
            } else {
                this.showToast(`Gagal update pesanan: ${data.message}`, 'error');
            }
        } catch (error) {
            console.error('Error handling order action:', error);
            this.showToast('An error occurred', 'error');
        }
    }

    async handlePaymentAction(orderNumber, action) {
        try {
            let paymentData = {
                orderNumber: orderNumber,
                paymentMethod: action.toUpperCase()
            };

            if (action === 'cash') {
                const amount = prompt('Enter cash amount tendered:');
                if (!amount || isNaN(amount)) {
                    this.showToast('Invalid amount', 'error');
                    return;
                }
                paymentData.cashAmount = parseFloat(amount);
            }

            const response = await fetch('/cashier/api/payments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...this.getCsrfHeaders()
                },
                body: JSON.stringify(paymentData)
            });

            const data = await response.json();

            if (data.success) {
                this.showToast('Payment processed successfully', 'success');
                this.loadDashboardData();

                if (window.webSocketClient) {
                    window.webSocketClient.sendOrderUpdate(data.data);
                }
            } else {
                this.showToast(`Payment failed: ${data.message}`, 'error');
            }
        } catch (error) {
            console.error('Error processing payment:', error);
            this.showToast('An error occurred while processing payment', 'error');
        }
    }

    viewOrder(orderNumber) {
        window.open(`/cashier/orders?order=${orderNumber}`, '_blank');
    }

    refreshData() {
        this.loadDashboardData();
        this.showToast('Data refreshed', 'info');
    }

    setupRealTimeUpdates() {
        // Setup WebSocket order update handler
        window.orderUpdateHandler = (orderUpdate) => {
            this.showToast(`Order ${orderUpdate.orderNumber} updated: ${orderUpdate.status}`, 'info');
            this.loadDashboardData(); // Refresh
        };
    }

    // Orders Page Functions
    async loadOrdersPage() {
        const ordersTableBody = document.getElementById('ordersTableBody');
        if (!ordersTableBody) return; 

        try {
            const response = await fetch('/cashier/api/orders/all');
            const data = await response.json();

            if (data.success && data.data) {
                this.updateOrdersTable(data.data);
            }
        } catch (error) {
            console.error('Error loading orders:', error);
        }

        
        await this.loadAvailableMenus();
    }

    async loadAvailableMenus() {
        try {
            console.log('Loading available menus...');
            const response = await fetch('/customer/api/menus');
            const data = await response.json();

            console.log('Menus response:', data);

            if (data.success && data.data) {
                this.availableMenus = data.data;
                console.log(`Loaded ${this.availableMenus.length} menus`);
            } else {
                console.error('Failed to load menus:', data);
                this.availableMenus = [];
            }
        } catch (error) {
            console.error('Error loading menus:', error);
            this.availableMenus = [];
        }
    }

    updateOrdersTable(orders) {
        const tbody = document.getElementById('ordersTableBody');
        if (!tbody) return;

        if (!orders || orders.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-muted py-3">
                        Tidak ada pesanan
                    </td>
                </tr>
            `;
            return;
        }

        let html = '';
        orders.forEach(order => {
            html += `
                <tr>
                    <td>${order.orderNumber}</td>
                    <td>${order.customerName || 'Walk-in'}</td>
                    <td>Rp ${order.total?.toLocaleString('id-ID') || '0'}</td>
                    <td>
                        <span class="badge bg-${this.getStatusColor(order.status)}">
                            ${order.status}
                        </span>
                    </td>
                    <td>
                        <span class="badge bg-${this.getPaymentStatusColor(order.paymentStatus)}">
                            ${order.paymentStatus}
                        </span>
                    </td>
                    <td>${this.formatDateTime(order.createdAt)}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary"
                                onclick="cashierApp.viewOrderDetails('${order.orderNumber}')">
                            <i class="fas fa-eye"></i>
                        </button>
                        ${this.getOrderActionButtons(order)}
                    </td>
                </tr>
            `;
        });

        tbody.innerHTML = html;
    }

    getPaymentStatusColor(status) {
        const colors = {
            'PENDING': 'warning',
            'PAID': 'success',
            'FAILED': 'danger'
        };
        return colors[status] || 'secondary';
    }

    getOrderActionButtons(order) {
        let buttons = '';

        // Status transition buttons based on current status
        switch (order.status) {
            case 'PENDING':
                buttons += `
                    <button class="btn btn-sm btn-success ms-1" title="Konfirmasi"
                            data-order-action="confirm" data-order-id="${order.id}">
                        <i class="fas fa-check"></i>
                    </button>
                    <button class="btn btn-sm btn-danger ms-1" title="Batalkan"
                            data-order-action="cancel" data-order-id="${order.id}">
                        <i class="fas fa-times"></i>
                    </button>
                `;
                break;

            case 'CONFIRMED':
                buttons += `
                    <button class="btn btn-sm btn-primary ms-1" title="Mulai Persiapan"
                            data-order-action="preparing" data-order-id="${order.id}">
                        <i class="fas fa-fire"></i>
                    </button>
                `;
                break;

            case 'PREPARING':
                buttons += `
                    <button class="btn btn-sm btn-info ms-1" title="Siap Diantar"
                            data-order-action="ready" data-order-id="${order.id}">
                        <i class="fas fa-clipboard-check"></i>
                    </button>
                `;
                break;

            case 'READY':
                
                if (order.paymentStatus === 'PENDING') {
                    buttons += `
                        <button class="btn btn-sm btn-warning ms-1" title="Proses Pembayaran"
                                onclick="cashierApp.showPaymentModal('${order.orderNumber}')">
                            <i class="fas fa-money-bill"></i>
                        </button>
                    `;
                } else {
                    buttons += `
                        <button class="btn btn-sm btn-success ms-1" title="Selesaikan Pesanan"
                                data-order-action="complete" data-order-id="${order.id}">
                            <i class="fas fa-check-double"></i>
                        </button>
                    `;
                }
                break;

            case 'COMPLETED':
                
                break;

            case 'CANCELLED':
                
                break;
        }

        return buttons;
    }

    addOrderItem() {
        const container = document.getElementById('orderItemsContainer');
        if (!container) return;

        console.log('Adding order item. Available menus:', this.availableMenus.length);

        const itemId = this.orderItemCounter++;

        const menuOptions = this.availableMenus.map(menu =>
            `<option value="${menu.id}" data-price="${menu.currentPrice}">
                ${menu.name} - Rp ${menu.currentPrice?.toLocaleString('id-ID')}
            </option>`
        ).join('');

        console.log('Menu options HTML length:', menuOptions.length);

        const itemHTML = `
            <div class="row mb-2 order-item" id="orderItem-${itemId}">
                <div class="col-md-6">
                    <select class="form-select menu-select" data-item-id="${itemId}" required>
                        <option value="">Pilih Menu</option>
                        ${menuOptions}
                    </select>
                </div>
                <div class="col-md-3">
                    <input type="number" class="form-control quantity-input"
                           data-item-id="${itemId}" min="1" value="1" placeholder="Qty" required>
                </div>
                <div class="col-md-2">
                    <input type="text" class="form-control subtotal-display"
                           id="subtotal-${itemId}" readonly placeholder="Subtotal">
                </div>
                <div class="col-md-1">
                    <button type="button" class="btn btn-danger btn-sm"
                            onclick="cashierApp.removeOrderItem(${itemId})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `;

        container.insertAdjacentHTML('beforeend', itemHTML);

        
        const menuSelect = container.querySelector(`.menu-select[data-item-id="${itemId}"]`);
        const qtyInput = container.querySelector(`.quantity-input[data-item-id="${itemId}"]`);

        menuSelect.addEventListener('change', () => this.updateItemSubtotal(itemId));
        qtyInput.addEventListener('input', () => this.updateItemSubtotal(itemId));
    }

    removeOrderItem(itemId) {
        const item = document.getElementById(`orderItem-${itemId}`);
        if (item) {
            item.remove();
        }
    }

    updateItemSubtotal(itemId) {
        const menuSelect = document.querySelector(`.menu-select[data-item-id="${itemId}"]`);
        const qtyInput = document.querySelector(`.quantity-input[data-item-id="${itemId}"]`);
        const subtotalDisplay = document.getElementById(`subtotal-${itemId}`);

        if (!menuSelect || !qtyInput || !subtotalDisplay) return;

        const selectedOption = menuSelect.options[menuSelect.selectedIndex];
        const price = parseFloat(selectedOption.dataset.price || 0);
        const quantity = parseInt(qtyInput.value || 0);
        const subtotal = price * quantity;

        subtotalDisplay.value = `Rp ${subtotal.toLocaleString('id-ID')}`;
    }

    async createNewOrder() {
        const customerName = document.getElementById('customerName').value.trim();
        const orderItems = [];

        
        document.querySelectorAll('.order-item').forEach(item => {
            const menuSelect = item.querySelector('.menu-select');
            const qtyInput = item.querySelector('.quantity-input');

            if (menuSelect && qtyInput && menuSelect.value) {
                orderItems.push({
                    menuId: parseInt(menuSelect.value),
                    quantity: parseInt(qtyInput.value)
                });
            }
        });

        
        if (!customerName) {
            this.showToast('Nama customer harus diisi', 'error');
            return;
        }

        if (orderItems.length === 0) {
            this.showToast('Tambahkan minimal 1 item menu', 'error');
            return;
        }

        try {
            const requestBody = {
                customerName: customerName,
                orderType: 'CASHIER_ASSISTED',
                items: orderItems
            };

            console.log('Creating order with data:', requestBody);

            const response = await fetch('/cashier/api/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...this.getCsrfHeaders()
                },
                body: JSON.stringify(requestBody)
            });

            const data = await response.json();
            console.log('Order creation response:', data);

            if (data.success) {
                this.showToast('Pesanan berhasil dibuat!', 'success');

                
                const modal = bootstrap.Modal.getInstance(document.getElementById('newOrderModal'));
                if (modal) modal.hide();

                
                document.getElementById('newOrderForm').reset();
                document.getElementById('orderItemsContainer').innerHTML = '';
                this.orderItemCounter = 0;

                
                this.loadOrdersPage();
            } else {
                this.showToast('Gagal membuat pesanan: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error creating order:', error);
            this.showToast('Terjadi kesalahan saat membuat pesanan', 'error');
        }
    }

    viewOrderDetails(orderNumber) {
        // TODO: Implement details view
        this.showToast(`Viewing order ${orderNumber}`, 'info');
    }

    async showPaymentModal(orderNumber) {
        try {
            // Fetch order details
            const response = await fetch(`/cashier/api/orders/all`);
            const data = await response.json();

            if (data.success && data.data) {
                const order = data.data.find(o => o.orderNumber === orderNumber);

                if (order) {
                    // Populate modal
                    document.getElementById('paymentOrderNumber').value = order.orderNumber;
                    document.getElementById('paymentTotal').value = `Rp ${order.total.toLocaleString('id-ID')}`;
                    document.getElementById('paymentTotal').dataset.total = order.total;

                    
                    document.getElementById('paymentMethod').value = 'CASH';
                    document.getElementById('cashAmount').value = '';
                    document.getElementById('changeAmount').value = '';
                    document.getElementById('qrTransactionCode').value = '';

                    // Show cash section by default
                    this.togglePaymentSections('CASH');

                    // Show modal
                    const modal = new bootstrap.Modal(document.getElementById('paymentModal'));
                    modal.show();
                } else {
                    this.showToast('Pesanan tidak ditemukan', 'error');
                }
            }
        } catch (error) {
            console.error('Error loading order for payment:', error);
            this.showToast('Gagal memuat data pesanan', 'error');
        }
    }

    async processPayment() {
        const orderNumber = document.getElementById('paymentOrderNumber').value;
        const paymentMethod = document.getElementById('paymentMethod').value;
        const totalElement = document.getElementById('paymentTotal');
        const total = parseFloat(totalElement.dataset.total || 0);

        let paymentData = {
            orderNumber: orderNumber,
            paymentMethod: paymentMethod
        };

        // Validation based on payment method
        if (paymentMethod === 'CASH') {
            const cashAmount = parseFloat(document.getElementById('cashAmount').value || 0);

            if (cashAmount < total) {
                this.showToast('Jumlah uang tidak cukup!', 'error');
                return;
            }

            paymentData.cashAmount = cashAmount;
        } else if (paymentMethod === 'QR_CODE') {
            const qrCode = document.getElementById('qrTransactionCode').value.trim();

            if (!qrCode) {
                this.showToast('Kode transaksi QR harus diisi!', 'error');
                return;
            }

            paymentData.qrData = qrCode;
        }

        try {
            console.log('Processing payment:', paymentData);

            const response = await fetch('/cashier/api/payments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...this.getCsrfHeaders()
                },
                body: JSON.stringify(paymentData)
            });

            const data = await response.json();
            console.log('Payment response:', data);

            if (data.success) {
                // Show change for cash payments
                if (paymentMethod === 'CASH' && data.data.change > 0) {
                    this.showToast(`Pembayaran berhasil! Kembalian: Rp ${data.data.change.toLocaleString('id-ID')}`, 'success');
                } else {
                    this.showToast('Pembayaran berhasil!', 'success');
                }

                
                const modal = bootstrap.Modal.getInstance(document.getElementById('paymentModal'));
                if (modal) modal.hide();

                // Refresh orders
                this.loadOrdersPage();
            } else {
                this.showToast(`Pembayaran gagal: ${data.message}`, 'error');
            }
        } catch (error) {
            console.error('Error processing payment:', error);
            this.showToast('Terjadi kesalahan saat memproses pembayaran', 'error');
        }
    }

    // Reports Page Functions
    setupReportsPage() {
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        const generateBtn = document.getElementById('generateReportBtn');

        if (!startDateInput || !endDateInput) {
            console.log('Not on reports page');
            return; // Not on reports page
        }

        console.log('Setting up reports page (v2)...');

        // Set default dates (today)
        const today = new Date().toISOString().split('T')[0];
        startDateInput.value = today;
        endDateInput.value = today;

        console.log('Default dates set to:', today);

        // Attach event listener to Generate button
        if (generateBtn) {
            generateBtn.addEventListener('click', () => {
                console.log('Generate button clicked (v2)');
                this.generateReport();
            });
            console.log('Generate button listener attached (v2)');
        }

        // Load today's report
        this.loadReportFromOrders();
    }

    async generateReport() {
        // UPDATED: 2025-11-17 22:26 - Fixed report generation
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        if (!startDate || !endDate) {
            this.showToast('Pilih tanggal mulai dan akhir', 'error');
            return;
        }

        console.log('*** GENERATING REPORT (NEW VERSION) for:', startDate, 'to', endDate);

        // Directly load report from orders
        this.loadReportFromOrders();
    }

    async loadReportFromOrders() {
        try {
            console.log('Loading report from orders...');
            const response = await fetch('/cashier/api/orders/all');
            const data = await response.json();

            console.log('Orders data received:', data);

            if (data.success && data.data) {
                const orders = data.data;
                console.log('Total orders in database:', orders.length);

                const startDate = new Date(document.getElementById('startDate').value);
                const endDate = new Date(document.getElementById('endDate').value);
                endDate.setHours(23, 59, 59, 999); // End of day

                console.log('Date range:', startDate, 'to', endDate);

                // Filter orders by date range
                const filteredOrders = orders.filter(order => {
                    const orderDate = new Date(order.createdAt);
                    return orderDate >= startDate && orderDate <= endDate;
                });

                console.log('Filtered orders:', filteredOrders.length);

                // Calculate totals
                const totalOrders = filteredOrders.length;
                const completedOrders = filteredOrders.filter(o => o.status === 'COMPLETED');
                const paidOrders = filteredOrders.filter(o => o.paymentStatus === 'PAID');
                const totalRevenue = paidOrders.reduce((sum, order) => sum + (order.total || 0), 0);

                console.log('Completed orders:', completedOrders.length);
                console.log('Paid orders:', paidOrders.length);
                console.log('Total revenue:', totalRevenue);

                // Update UI
                const revenueElement = document.getElementById('totalRevenue');
                const ordersElement = document.getElementById('totalOrders');

                console.log('Revenue element:', revenueElement);
                console.log('Orders element:', ordersElement);

                if (revenueElement) {
                    revenueElement.textContent = `Rp ${totalRevenue.toLocaleString('id-ID')}`;
                    console.log('Updated revenue to:', revenueElement.textContent);
                } else {
                    console.error('Revenue element not found!');
                }

                if (ordersElement) {
                    ordersElement.textContent = totalOrders;
                    console.log('Updated orders to:', ordersElement.textContent);
                } else {
                    console.error('Orders element not found!');
                }

                this.showToast('Laporan berhasil di-generate', 'success');
            } else {
                console.error('Failed to load orders:', data);
            }
        } catch (error) {
            console.error('Error loading report:', error);
        }
    }

    exportReport() {
        this.showToast('Export PDF sedang dalam pengembangan', 'info');
    }

    showToast(message, type = 'info') {
        // Use the WebSocket client's notification system if available
        if (window.webSocketClient && window.webSocketClient.showNotification) {
            window.webSocketClient.showNotification(message, type);
            return;
        }

        // Create Bootstrap toast
        const toastContainer = this.getOrCreateToastContainer();

        const toastId = 'toast-' + Date.now();
        const bgClass = this.getToastBgClass(type);
        const icon = this.getToastIcon(type);

        const toastHTML = `
            <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="fas ${icon} me-2"></i>${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;

        toastContainer.insertAdjacentHTML('beforeend', toastHTML);

        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement, {
            autohide: true,
            delay: 3000
        });

        toast.show();

        // Remove toast element after it's hidden
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    getOrCreateToastContainer() {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container position-fixed top-0 end-0 p-3';
            container.style.zIndex = '9999';
            document.body.appendChild(container);
        }
        return container;
    }

    getToastBgClass(type) {
        const bgClasses = {
            'success': 'bg-success',
            'error': 'bg-danger',
            'warning': 'bg-warning',
            'info': 'bg-info'
        };
        return bgClasses[type] || 'bg-info';
    }

    getToastIcon(type) {
        const icons = {
            'success': 'fa-check-circle',
            'error': 'fa-exclamation-circle',
            'warning': 'fa-exclamation-triangle',
            'info': 'fa-info-circle'
        };
        return icons[type] || 'fa-info-circle';
    }

    // Menu management functions
    async toggleMenuAvailability(menuId) {
        try {
            const response = await fetch(`/cashier/api/menus/${menuId}/availability`, {
                method: 'PUT',
                headers: {
                    ...this.getCsrfHeaders()
                }
            });
            const data = await response.json();

            if (data.success) {
                this.showToast('Status menu berhasil diupdate', 'success');
                // Reload the page to show updated status
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                this.showToast('Gagal update menu: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error toggling menu availability:', error);
            this.showToast('Terjadi kesalahan', 'error');
        }
    }

    async editMenu(menuId) {
        try {
            // Fetch all menus and categories
            const [menusResponse, categoriesResponse] = await Promise.all([
                fetch('/customer/api/menus'),
                fetch('/cashier/api/categories')
            ]);

            const menusData = await menusResponse.json();
            const categoriesData = await categoriesResponse.json();

            if (menusData.success && menusData.data) {
                const menu = menusData.data.find(m => m.id === menuId);
                if (menu) {
                    const categories = categoriesData.success ? categoriesData.data : [];
                    this.showEditMenuModal(menu, categories);
                } else {
                    this.showToast('Menu tidak ditemukan', 'error');
                }
            }
        } catch (error) {
            console.error('Error loading menu:', error);
            this.showToast('Gagal memuat data menu', 'error');
        }
    }

    showEditMenuModal(menu, categories) {
        // Create modal dynamically if it doesn't exist
        let modal = document.getElementById('editMenuModal');
        if (!modal) {
            const modalHTML = `
                <div class="modal fade" id="editMenuModal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Edit Menu</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" id="editMenuId">
                                <div class="mb-3">
                                    <label class="form-label">Nama Menu</label>
                                    <input type="text" class="form-control" id="editMenuName" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Kategori</label>
                                    <select class="form-select" id="editMenuCategoryId" required>
                                        <option value="">Pilih Kategori</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Deskripsi</label>
                                    <textarea class="form-control" id="editMenuDescription" rows="3"></textarea>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Harga Normal</label>
                                    <input type="number" class="form-control" id="editMenuPrice" required>
                                </div>
                                <div class="mb-3">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="editMenuIsPromo">
                                        <label class="form-check-label" for="editMenuIsPromo">
                                            Menu Promo
                                        </label>
                                    </div>
                                </div>
                                <div class="mb-3" id="editPromoSection" style="display: none;">
                                    <label class="form-label">Harga Promo</label>
                                    <input type="number" class="form-control" id="editMenuPromoPrice">
                                </div>
                                <div class="mb-3">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="editMenuAvailable">
                                        <label class="form-check-label" for="editMenuAvailable">
                                            Tersedia
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                                <button type="button" class="btn btn-primary" id="saveMenuBtn">Simpan</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            document.body.insertAdjacentHTML('beforeend', modalHTML);
            modal = document.getElementById('editMenuModal');

            // Add event listener for promo checkbox
            document.getElementById('editMenuIsPromo').addEventListener('change', (e) => {
                document.getElementById('editPromoSection').style.display = e.target.checked ? 'block' : 'none';
            });

            // Add event listener for save button
            document.getElementById('saveMenuBtn').addEventListener('click', () => {
                this.saveMenuChanges();
            });
        }

        // Populate category dropdown
        const categorySelect = document.getElementById('editMenuCategoryId');
        categorySelect.innerHTML = '<option value="">Pilih Kategori</option>';
        categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.name;
            categorySelect.appendChild(option);
        });

        // Populate modal with menu data
        document.getElementById('editMenuId').value = menu.id;
        document.getElementById('editMenuName').value = menu.name;
        document.getElementById('editMenuCategoryId').value = menu.category ? menu.category.id : '';
        document.getElementById('editMenuDescription').value = menu.description || '';
        document.getElementById('editMenuPrice').value = menu.price;
        document.getElementById('editMenuIsPromo').checked = menu.isPromo || false;
        document.getElementById('editMenuPromoPrice').value = menu.promoPrice || '';
        document.getElementById('editMenuAvailable').checked = menu.available;

        // Show/hide promo section
        document.getElementById('editPromoSection').style.display = menu.isPromo ? 'block' : 'none';

        // Show modal
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    }

    async saveMenuChanges() {
        const menuId = document.getElementById('editMenuId').value;
        const categoryId = document.getElementById('editMenuCategoryId').value;

        if (!categoryId) {
            this.showToast('Pilih kategori terlebih dahulu', 'error');
            return;
        }

        const menuData = {
            name: document.getElementById('editMenuName').value,
            description: document.getElementById('editMenuDescription').value,
            categoryId: parseInt(categoryId),
            price: parseFloat(document.getElementById('editMenuPrice').value),
            isPromo: document.getElementById('editMenuIsPromo').checked,
            promoPrice: document.getElementById('editMenuIsPromo').checked ?
                parseFloat(document.getElementById('editMenuPromoPrice').value || 0) : null,
            available: document.getElementById('editMenuAvailable').checked
        };

        try {
            const response = await fetch(`/cashier/api/menus/${menuId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    ...this.getCsrfHeaders()
                },
                body: JSON.stringify(menuData)
            });

            const data = await response.json();

            if (data.success) {
                this.showToast('Menu berhasil diupdate', 'success');

                
                const modal = bootstrap.Modal.getInstance(document.getElementById('editMenuModal'));
                if (modal) modal.hide();

                // Reload page
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                this.showToast('Gagal update menu: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error updating menu:', error);
            this.showToast('Terjadi kesalahan saat update menu', 'error');
        }
    }

    async deleteCategory(categoryId) {
        if (!confirm('Apakah Anda yakin ingin menghapus kategori ini?')) {
            return;
        }

        try {
            const response = await fetch(`/cashier/api/categories/${categoryId}`, {
                method: 'DELETE',
                headers: {
                    ...this.getCsrfHeaders()
                }
            });

            const data = await response.json();

            if (data.success) {
                this.showToast('Kategori berhasil dihapus', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                this.showToast('Gagal hapus kategori: ' + data.message, 'error');
            }
        } catch (error) {
            console.error('Error deleting category:', error);
            this.showToast('Terjadi kesalahan', 'error');
        }
    }

}

// Initialize cashier app
const cashierApp = new CashierApp();
