class AuthApp {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.checkExistingSession();
    }

    setupEventListeners() {
        // Login form validation
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Password visibility toggle
        const togglePassword = document.getElementById('togglePassword');
        if (togglePassword) {
            togglePassword.addEventListener('click', () => this.togglePasswordVisibility());
        }
    }

    async handleLogin(event) {
        event.preventDefault();

        const form = event.target;
        const formData = new FormData(form);
        const username = formData.get('username');
        const password = formData.get('password');

        // Basic validation
        if (!username || !password) {
            this.showError('Please enter both username and password');
            return;
        }

        // Show loading state
        this.setLoadingState(true);

        try {
            const response = await fetch('/auth/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            const data = await response.json();

            if (data.success) {
                // Store session token
                localStorage.setItem('sessionToken', data.data.sessionToken);
                localStorage.setItem('cashierData', JSON.stringify(data.data.cashier));

                this.showSuccess('Login successful! Redirecting...');

                // Redirect to dashboard
                setTimeout(() => {
                    window.location.href = '/cashier/dashboard';
                }, 1000);
            } else {
                this.showError(data.message || 'Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showError('An error occurred during login');
        } finally {
            this.setLoadingState(false);
        }
    }

    async checkExistingSession() {
        const sessionToken = localStorage.getItem('sessionToken');
        if (!sessionToken) return;

        try {
            const response = await fetch('/auth/api/validate', {
                headers: {
                    'X-Session-Token': sessionToken
                }
            });

            if (response.ok) {
                // Session is valid, redirect to dashboard
                window.location.href = '/cashier/dashboard';
            } else {
                // Session is invalid, clear stored data
                this.clearSessionData();
            }
        } catch (error) {
            console.error('Session validation error:', error);
            this.clearSessionData();
        }
    }

    clearSessionData() {
        localStorage.removeItem('sessionToken');
        localStorage.removeItem('cashierData');
    }

    togglePasswordVisibility() {
        const passwordInput = document.getElementById('password');
        const toggleIcon = document.getElementById('togglePassword');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            toggleIcon.className = 'fas fa-eye-slash';
        } else {
            passwordInput.type = 'password';
            toggleIcon.className = 'fas fa-eye';
        }
    }

    setLoadingState(loading) {
        const submitButton = document.querySelector('#loginForm button[type="submit"]');
        if (!submitButton) return;

        if (loading) {
            submitButton.disabled = true;
            submitButton.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Logging in...';
        } else {
            submitButton.disabled = false;
            submitButton.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i>Login';
        }
    }

    showError(message) {
        this.showMessage(message, 'error');
    }

    showSuccess(message) {
        this.showMessage(message, 'success');
    }

    showMessage(message, type) {
        // Remove existing messages
        const existingAlerts = document.querySelectorAll('.alert-dismissible');
        existingAlerts.forEach(alert => alert.remove());

        // Create new alert
        const alert = document.createElement('div');
        alert.className = `alert alert-${type === 'error' ? 'danger' : 'success'} alert-dismissible fade show`;
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        // Insert at the top of the form
        const form = document.getElementById('loginForm');
        if (form) {
            form.insertBefore(alert, form.firstChild);
        }

        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (alert.parentNode) {
                alert.remove();
            }
        }, 5000);
    }

    // Logout function
    async logout() {
        const sessionToken = localStorage.getItem('sessionToken');

        if (sessionToken) {
            try {
                await fetch('/auth/api/logout', {
                    method: 'POST',
                    headers: {
                        'X-Session-Token': sessionToken
                    }
                });
            } catch (error) {
                console.error('Logout error:', error);
            }
        }

        this.clearSessionData();
        window.location.href = '/auth/login';
    }
}

// Initialize auth app
const authApp = new AuthApp();