// ==========================================================================
// ⚡ Flare Docs Interactive Controller - JS
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initScrollSpy();
    initSimulator();
});

// 1. Theme Configuration (Persisted in localStorage)
function initTheme() {
    const themeToggle = document.getElementById('theme-toggle');
    const savedTheme = localStorage.getItem('theme') || 'light';
    
    if (savedTheme === 'dark') {
        document.body.classList.remove('light-theme');
        document.body.classList.add('dark-theme');
    } else {
        document.body.classList.remove('dark-theme');
        document.body.classList.add('light-theme');
    }

    themeToggle.addEventListener('click', () => {
        if (document.body.classList.contains('light-theme')) {
            document.body.classList.remove('light-theme');
            document.body.classList.add('dark-theme');
            localStorage.setItem('theme', 'dark');
        } else {
            document.body.classList.remove('dark-theme');
            document.body.classList.add('light-theme');
            localStorage.setItem('theme', 'light');
        }
    });
}

// 2. Tab Switcher for Code Examples
window.switchTab = function(event, tabId) {
    const tabContainer = event.currentTarget.closest('.tab-container');
    
    // Deactivate all headers and content boxes in this container
    tabContainer.querySelectorAll('.tab-link').forEach(btn => btn.classList.remove('active'));
    tabContainer.querySelectorAll('.tab-content').forEach(box => box.classList.remove('active'));
    
    // Activate target elements
    event.currentTarget.classList.add('active');
    document.getElementById(tabId).classList.add('active');
}

// 3. ScrollSpy Navigation
function initScrollSpy() {
    const sections = document.querySelectorAll('.doc-section');
    const navLinks = document.querySelectorAll('.nav-menu a');

    window.addEventListener('scroll', () => {
        let currentSectionId = "";
        const scrollPosition = window.scrollY + 100;

        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.offsetHeight;

            if (scrollPosition >= sectionTop && scrollPosition < sectionTop + sectionHeight) {
                currentSectionId = section.getAttribute('id');
            }
        });

        if (currentSectionId) {
            navLinks.forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('href') === `#${currentSectionId}`) {
                    link.classList.add('active');
                }
            });
        }
    });
}

// 4. Interactive Android Phone Simulator
function initSimulator() {
    const triggerBtn = document.getElementById('trigger-btn');
    const alertBox = document.getElementById('simulated-alert');
    const iconSpan = document.getElementById('sim-icon');
    const titleSpan = document.getElementById('sim-title');
    const descSpan = document.getElementById('sim-desc');
    const progressDiv = document.getElementById('sim-progress');
    const actionBtn = document.getElementById('sim-action');
    
    let dismissTimeout = null;

    triggerBtn.addEventListener('click', () => {
        // Clear previous state and animations
        if (dismissTimeout) {
            clearTimeout(dismissTimeout);
        }
        alertBox.className = 'sim-alert hidden';
        progressDiv.className = 'sim-alert-progress';
        // Force reflow to reset animations
        void alertBox.offsetWidth;

        // Retrieve control values
        const type = document.getElementById('ctrl-type').value;
        const position = document.getElementById('ctrl-position').value;
        const message = document.getElementById('ctrl-msg').value;
        const showProgress = document.getElementById('ctrl-progress').checked;

        // Configure type styling and icon
        let iconHtml = 'ℹ️';
        let typeName = 'Info';
        let bgStyle = 'var(--toast-info)';

        switch(type) {
            case 'SUCCESS':
                iconHtml = '✓';
                typeName = 'Success';
                bgStyle = 'var(--toast-success)';
                break;
            case 'ERROR':
                iconHtml = '✗';
                typeName = 'Error';
                bgStyle = 'var(--toast-error)';
                break;
            case 'WARNING':
                iconHtml = '⚠';
                typeName = 'Warning';
                bgStyle = 'var(--toast-warning)';
                break;
            case 'LOADING':
                iconHtml = '<span class="spinner-icon"></span>';
                typeName = 'Loading';
                bgStyle = 'var(--toast-loading)';
                break;
            case 'INFO':
            default:
                iconHtml = 'ℹ️';
                typeName = 'Info';
                bgStyle = 'var(--toast-info)';
                break;
        }

        iconSpan.innerHTML = iconHtml;
        titleSpan.textContent = typeName;
        descSpan.textContent = message;
        alertBox.style.backgroundColor = bgStyle;

        // Apply placement class
        const posClass = `pos-${position.toLowerCase()}`;
        alertBox.classList.add(posClass);

        // Progress bar configuration
        if (showProgress && type !== 'LOADING') {
            progressDiv.style.display = 'block';
            // Trigger animation with a 3-second duration transition
            progressDiv.style.transition = 'none';
            progressDiv.style.transform = 'scaleX(1)';
            void progressDiv.offsetWidth; // Force layout pass
            
            progressDiv.style.transition = 'transform 3000ms linear';
            progressDiv.style.transform = 'scaleX(0)';
        } else {
            progressDiv.style.display = 'none';
        }

        // Show the alert box
        alertBox.classList.remove('hidden');

        // Auto-dismiss execution
        dismissTimeout = setTimeout(() => {
            alertBox.classList.add('hidden');
        }, 3000);
    });

    // Action button manual dismiss click action
    actionBtn.addEventListener('click', () => {
        if (dismissTimeout) {
            clearTimeout(dismissTimeout);
        }
        alertBox.classList.add('hidden');
    });
}
