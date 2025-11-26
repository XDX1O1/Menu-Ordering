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
     * @param {string|Array} dateString - ISO date string from server or array [year, month, day, hour, minute, second]
     * @returns {Date}
     */
    parseDate(dateString) {
        if (!dateString) return null;

        // Handle array format from Jackson serialization
        if (Array.isArray(dateString)) {
            const [year, month, day, hour = 0, minute = 0, second = 0] = dateString;
            const isoString = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`;
            return new Date(isoString);
        }

        return new Date(dateString);
    },

    /**
     * Format date to Indonesian locale with Asia/Jakarta timezone
     * @param {Date|string|Array} date - Date object, ISO string, or array
     * @param {object} options - Intl.DateTimeFormat options
     * @returns {string}
     */
    formatDate(date, options = {}) {
        if (!date) return '';

        // Parse date if it's a string or array, otherwise use as-is
        let dateObj;
        if (typeof date === 'string' || Array.isArray(date)) {
            dateObj = this.parseDate(date);
        } else {
            dateObj = date;
        }

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
     * @param {Date|string|Array} date - Date object, ISO string, or array [year, month, day, hour, minute, second]
     * @param {object} options - Intl.DateTimeFormat options
     * @returns {string}
     */
    formatDateTime(date, options = {}) {
        if (!date) return '';

        // Parse date if it's a string or array, otherwise use as-is
        let dateObj;
        if (typeof date === 'string' || Array.isArray(date)) {
            dateObj = this.parseDate(date);
        } else {
            dateObj = date;
        }

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
     * @param {Date|string|Array} date - Date object, ISO string, or array
     * @param {object} options - Intl.DateTimeFormat options
     * @returns {string}
     */
    formatTime(date, options = {}) {
        if (!date) return '';

        // Parse date if it's a string or array, otherwise use as-is
        let dateObj;
        if (typeof date === 'string' || Array.isArray(date)) {
            dateObj = this.parseDate(date);
        } else {
            dateObj = date;
        }

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
     * @param {Date|string|Array} date
     * @returns {string}
     */
    getRelativeTime(date) {
        if (!date) return '';

        // Parse date if it's a string or array, otherwise use as-is
        let dateObj;
        if (typeof date === 'string' || Array.isArray(date)) {
            dateObj = this.parseDate(date);
        } else {
            dateObj = date;
        }

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
