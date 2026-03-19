// turnTheme.js
const ThemeManager = {
    // 初始化主题
    init() {
        // 检查本地存储中的主题偏好
        const savedTheme = localStorage.getItem('blog-theme');
        // 检查系统主题设置
        const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

        // 确定初始主题
        if (savedTheme === 'dark' || (!savedTheme && systemPrefersDark)) {
            this.enableDarkMode();
        } else {
            this.enableLightMode();
        }

        // 监听系统主题变化
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
            if (!localStorage.getItem('blog-theme')) {
                if (e.matches) {
                    this.enableDarkMode();
                } else {
                    this.enableLightMode();
                }
            }
        });
    },

    // 启用深色模式
    enableDarkMode() {
        document.documentElement.classList.add('dark-theme');
        localStorage.setItem('blog-theme', 'dark');
        // 更新主题切换按钮状态
        this.updateThemeButton();
    },

    // 启用浅色模式
    enableLightMode() {
        document.documentElement.classList.remove('dark-theme');
        localStorage.setItem('blog-theme', 'light');
        // 更新主题切换按钮状态
        this.updateThemeButton();
    },

    // 切换主题
    toggleTheme() {
        if (document.documentElement.classList.contains('dark-theme')) {
            this.enableLightMode();
        } else {
            this.enableDarkMode();
        }
    },

    // 更新主题切换按钮状态
    updateThemeButton() {
        const isDark = document.documentElement.classList.contains('dark-theme');
        const themeButton = document.getElementById('theme-toggle');
        if (themeButton) {
            themeButton.textContent = isDark ? '🌞 浅色模式' : '🌙 深色模式';
            themeButton.title = isDark ? '切换到浅色模式' : '切换到深色模式';
        }
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    ThemeManager.init();

    // 绑定主题切换按钮事件
    const themeButton = document.getElementById('theme-toggle');
    if (themeButton) {
        themeButton.addEventListener('click', () => ThemeManager.toggleTheme());
    }
});