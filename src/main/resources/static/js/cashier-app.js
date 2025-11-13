class CashierApp {
    constructor() {
        this.currentCashier = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupDashboardEventListeners(); // Add this line
        this.loadDashboardData();
        this.setupRealTimeUpdates();
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

            const response = await fetch(url, { method: method });
            const data = await response.json();

            if (data.success) {
                this.showToast(`Order ${action}ed successfully`, 'success');
                this.loadDashboardData(); // Refresh data

                // Send WebSocket update
                if (window.webSocketClient) {
                    window.webSocketClient.sendOrderUpdate(data.data);
                }
            } else {
                this.showToast(`Failed to ${action} order: ${data.message}`, 'error');
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
            this.loadDashboardData(); // Refresh data
        };
    }

    showToast(message, type = 'info') {
        // Use the WebSocket client's notification system if available
        if (window.webSocketClient) {
            window.webSocketClient.showNotification(message, type);
        } else {
            // Fallback to simple alert
            alert(`${type.toUpperCase()}: ${message}`);
        }
    }

    // Menu management functions
    async toggleMenuAvailability(menuId) {
        try {
            const response = await fetch(`/cashier/api/menus/${menuId}/availability`, {
                method: 'PUT'
            });
            const data = await response.json();

            if (data.success) {
                this.showToast('Menu availability updated', 'success');
                return data.data;
            } else {
                this.showToast('Failed to update menu availability', 'error');
            }
        } catch (error) {
            console.error('Error toggling menu availability:', error);
            this.showToast('An error occurred', 'error');
        }
    }

    // Report generation
    async generateReport(reportType, startDate, endDate) {
        try {
            const response = await fetch('/api/reports/sales', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    startDate: startDate,
                    endDate: endDate,
                    reportType: reportType
                })
            });

            const data = await response.json();

            if (data.success) {
                return data.data;
            } else {
                this.showToast('Failed to generate report: ' + data.message, 'error');
                return null;
            }
        } catch (error) {
            console.error('Error generating report:', error);
            this.showToast('An error occurred while generating report', 'error');
            return null;
        }
    }
}

// Initialize cashier app
const cashierApp = new CashierApp();
