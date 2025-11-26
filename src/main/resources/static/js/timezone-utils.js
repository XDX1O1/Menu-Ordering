// Timezone Utility for Asia/Jakarta (WIB, UTC+7)
const TimezoneUtils = {
    // Timezone constant
    TIMEZONE: 'Asia/Jakarta',
    LOCALE: 'id-ID',

    /**
     * Get current date/time in Asia/Jakarta timezone
     */
    now() {
        return new Date();
    },

    /**
     * Parse date string to Date object
     * @param {string} dateString - ISO date string from server
     * @returns {Date}
     */
    parseDate(dateString) {
        if (!dateString) return null;
        return new Date(dateString);
    },

    /**
     * Format date to Indonesian locale with Asia/Jakarta timezone
     * @param {Date|string} date - Date object or ISO string
     * @param {object} options - Intl.DateTimeFormat options
     * @returns {string}
     */
    formatDate(date, options = {}) {
        if (!date) return '';

        const dateObj = typeof date === 'string' ? this.parseDate(date) : date;
        if (!dateObj) return '';

        const defaultOptions = {
            timeZone: this.TIMEZONE,
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            ...options
        };

        return new Intl.DateTimeFormat(this.LOCALE, defaultOptions).format(dateObj);
    },

    /**
     * Format date and time to Indonesian locale with Asia/Jakarta timezone
     * @param {Date|string} date - Date object or ISO string
     * @param {object} options - Intl.DateTimeFormat options
     * @returns {string}
     */
    formatDateTime(date, options = {}) {
        if (!date) return '';

        const dateObj = typeof date === 'string' ? this.parseDate(date) : date;
        if (!dateObj) return '';

        // Check if date is valid
        if (isNaN(dateObj.getTime())) {
            console.error('Invalid date object:', date);
            return '';
        }

        const defaultOptions = {
            timeZone: this.TIMEZONE,
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false,
            ...options
        };

        try {
            return new Intl.DateTimeFormat(this.LOCALE, defaultOptions).format(dateObj);
        } catch (e) {
            console.error('Error formatting date:', date, e);
            return '';
        }
    },

    /**
     * Format time only to Indonesian locale with Asia/Jakarta timezone
     * @param {Date|string} date - Date object or ISO string
     * @param {object} options - Intl.DateTimeFormat options
     * @returns {string}
     */
    formatTime(date, options = {}) {
        if (!date) return '';

        const dateObj = typeof date === 'string' ? this.parseDate(date) : date;
        if (!dateObj) return '';

        const defaultOptions = {
            timeZone: this.TIMEZONE,
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false,
            ...options
        };

        return new Intl.DateTimeFormat(this.LOCALE, defaultOptions).format(dateObj);
    },

    /**
     * Get date in YYYY-MM-DD format for Asia/Jakarta timezone
     * @param {Date} date - Date object (defaults to now)
     * @returns {string}
     */
    toDateString(date = null) {
        const dateObj = date || this.now();

        // Get date parts in Asia/Jakarta timezone
        const formatter = new Intl.DateTimeFormat(this.LOCALE, {
            timeZone: this.TIMEZONE,
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });

        const parts = formatter.formatToParts(dateObj);
        const year = parts.find(p => p.type === 'year').value;
        const month = parts.find(p => p.type === 'month').value;
        const day = parts.find(p => p.type === 'day').value;

        return `${year}-${month}-${day}`;
    },

    /**
     * Format currency in Indonesian Rupiah
     * @param {number} amount
     * @returns {string}
     */
    formatCurrency(amount) {
        if (amount === null || amount === undefined) return 'Rp 0';
        return 'Rp ' + amount.toLocaleString(this.LOCALE, {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        });
    },

    /**
     * Get relative time string (e.g., "2 minutes ago")
     * @param {Date|string} date
     * @returns {string}
     */
    getRelativeTime(date) {
        if (!date) return '';

        const dateObj = typeof date === 'string' ? this.parseDate(date) : date;
        if (!dateObj) return '';

        const now = this.now();
        const diffMs = now - dateObj;
        const diffSeconds = Math.floor(diffMs / 1000);
        const diffMinutes = Math.floor(diffSeconds / 60);
        const diffHours = Math.floor(diffMinutes / 60);
        const diffDays = Math.floor(diffHours / 24);

        if (diffSeconds < 60) return 'baru saja';
        if (diffMinutes < 60) return `${diffMinutes} menit yang lalu`;
        if (diffHours < 24) return `${diffHours} jam yang lalu`;
        if (diffDays < 7) return `${diffDays} hari yang lalu`;

        return this.formatDate(dateObj);
    }
};

// Make it available globally
window.TimezoneUtils = TimezoneUtils;
