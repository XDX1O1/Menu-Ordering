// Menu Category Filter Script with Backend Cart Integration
// Location: src/main/resources/static/js/menu-filter.js

document.addEventListener('DOMContentLoaded', function() {
    // Get all category buttons
    const categoryButtons = document.querySelectorAll('.category-btn');
    const menuItems = document.querySelectorAll('.menu-card');
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');

    // Get CSRF token for security
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    // ========== CATEGORY FILTER (CLIENT-SIDE) ==========
    function filterByCategory(category) {
        menuItems.forEach(item => {
            const itemCategory = item.getAttribute('data-category');

            if (category === 'SEMUA' || itemCategory === category) {
                item.closest('.col-xl-3').style.display = 'block';
            } else {
                item.closest('.col-xl-3').style.display = 'none';
            }
        });
    }

    // ========== SEARCH FUNCTION (CLIENT-SIDE) ==========
    function searchMenu(searchTerm) {
        const term = searchTerm.toLowerCase();

        menuItems.forEach(item => {
            const menuName = item.querySelector('.card-title').textContent.toLowerCase();
            const menuDesc = item.querySelector('.card-text').textContent.toLowerCase();

            if (menuName.includes(term) || menuDesc.includes(term)) {
                item.closest('.col-xl-3').style.display = 'block';
            } else {
                item.closest('.col-xl-3').style.display = 'none';
            }
        });
    }

    // ========== CATEGORY BUTTON CLICKS ==========
    categoryButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            // Remove active class from all buttons
            categoryButtons.forEach(btn => btn.classList.remove('active'));

            // Add active class to clicked button
            this.classList.add('active');

            // Get category from data attribute
            const category = this.getAttribute('data-category');

            // Filter menu items
            filterByCategory(category);

            // Clear search input when filtering by category
            if (searchInput) searchInput.value = '';
        });
    });

    // ========== SEARCH BUTTON CLICK ==========
    if (searchButton) {
        searchButton.addEventListener('click', function() {
            const searchTerm = searchInput.value;
            searchMenu(searchTerm);

            // Remove active class from all category buttons
            categoryButtons.forEach(btn => btn.classList.remove('active'));
        });
    }

    // ========== SEARCH ON ENTER KEY ==========
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const searchTerm = this.value;
                searchMenu(searchTerm);

                // Remove active class from all category buttons
                categoryButtons.forEach(btn => btn.classList.remove('active'));
            }
        });

        // Clear search and show all when input is cleared
        searchInput.addEventListener('input', function() {
            if (this.value === '') {
                menuItems.forEach(item => {
                    item.closest('.col-xl-3').style.display = 'block';
                });
            }
        });
    }

    // ========== QUANTITY INCREASE BUTTON ==========
    document.querySelectorAll('.btn-increase').forEach(button => {
        button.addEventListener('click', function() {
            const menuId = this.getAttribute('data-menu-id');
            const input = document.querySelector(`input[data-menu-id="${menuId}"]`);
            const currentValue = parseInt(input.value);
            const maxValue = parseInt(input.getAttribute('max'));

            if (currentValue < maxValue) {
                input.value = currentValue + 1;
            }
        });
    });

    // ========== QUANTITY DECREASE BUTTON ==========
    document.querySelectorAll('.btn-decrease').forEach(button => {
        button.addEventListener('click', function() {
            const menuId = this.getAttribute('data-menu-id');
            const input = document.querySelector(`input[data-menu-id="${menuId}"]`);
            const currentValue = parseInt(input.value);
            const minValue = parseInt(input.getAttribute('min'));

            if (currentValue > minValue) {
                input.value = currentValue - 1;
            }
        });
    });

    // ========== ADD TO CART (BACKEND INTEGRATION) ==========
    document.querySelectorAll('.btn-add-to-cart').forEach(button => {
        button.addEventListener('click', function() {
            const menuId = this.getAttribute('data-menu-id');
            const quantityInput = document.querySelector(`input[data-menu-id="${menuId}"]`);
            const quantity = parseInt(quantityInput.value);

            // Get menu name for notification
            const card = this.closest('.menu-card');
            const menuName = card.querySelector('.card-title').textContent;

            // Disable button to prevent double-click
            this.disabled = true;
            const originalHTML = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i> Menambahkan...';

            // Prepare request headers
            const headers = {
                'Content-Type': 'application/json'
            };

            // Add CSRF token if available
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            // AJAX call to backend
            fetch('/customer/api/cart/add', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({
                    menuId: parseInt(menuId),
                    quantity: quantity
                })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Success - show message
                        showNotification(`${menuName} (${quantity}x) berhasil ditambahkan!`, 'success');

                        // Reset quantity to 1
                        quantityInput.value = 1;

                        // Update cart count in header
                        updateCartCount();
                    } else {
                        // Error from backend
                        showNotification(data.message || 'Gagal menambahkan ke keranjang', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Gagal menambahkan ke keranjang. Silakan coba lagi.', 'error');
                })
                .finally(() => {
                    // Re-enable button
                    this.disabled = false;
                    this.innerHTML = originalHTML;
                });
        });
    });

    // ========== HELPER: SHOW NOTIFICATION ==========
    function showNotification(message, type) {
        // Remove existing notifications
        document.querySelectorAll('.notification-toast').forEach(n => n.remove());

        // Create notification element
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show position-fixed notification-toast`;
        notification.style.cssText = 'top: 80px; right: 20px; z-index: 9999; min-width: 300px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);';
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(notification);

        // Auto-remove after 3 seconds
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 150);
        }, 3000);
    }

    // ========== HELPER: UPDATE CART COUNT ==========
    function updateCartCount() {
        fetch('/customer/api/cart/count')
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data) {
                    const count = data.data.count;
                    const cartBadge = document.querySelector('.cart-count-badge');
                    if (cartBadge) {
                        cartBadge.textContent = count;
                        cartBadge.style.display = count > 0 ? 'inline-block' : 'none';
                    }

                    // Update cart button text if exists
                    const cartButton = document.querySelector('.btn-cart');
                    if (cartButton && count > 0) {
                        cartButton.classList.add('btn-cart-active');
                    }
                }
            })
            .catch(error => console.error('Error updating cart count:', error));
    }

    // ========== INITIALIZE CART COUNT ON PAGE LOAD ==========
    updateCartCount();

    // ========== VIEW CART BUTTON ==========
    const viewCartBtn = document.getElementById('viewCartBtn');
    if (viewCartBtn) {
        viewCartBtn.addEventListener('click', function() {
            window.location.href = '/customer/cart';
        });
    }
});