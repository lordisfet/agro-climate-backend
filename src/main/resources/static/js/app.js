const AppController = {
    // Ссылка на главный контейнер в HTML
    container: document.getElementById('charts-container'),

    // Создает HTML-карточку для нового датчика, если её еще нет
    createDomWrapperIfNeeded(devEUI, location) {
        if (!document.getElementById(`wrapper-${devEUI}`)) {
            const wrapper = document.createElement('div');
            wrapper.id = `wrapper-${devEUI}`;
            wrapper.className = 'chart-wrapper';
            wrapper.innerHTML = `
                <h3 class="chart-title">Локація: ${location} <br><small style="color:gray; font-size: 0.8em;">ID: ${devEUI}</small></h3>
                <div class="canvas-container">
                    <canvas id="chart-${devEUI}"></canvas>
                </div>
            `;
            this.container.appendChild(wrapper);
        }
    },

    // Главная функция синхронизации
    async syncData() {
        try {
            // 1. Идем на бэкенд за списком датчиков
            const nodes = await ApiClient.getNodes();
            
            if (nodes.length === 0) {
                this.container.innerHTML = '<p style="text-align:center; color:#666;">Активні датчики не знайдено.</p>';
                return;
            }

            // 2. Для каждого датчика...
            for (const node of nodes) {
                const devEUI = node.devEUI;
                const location = node.location || 'Невідома локація';

                // Создаем для него блок на странице
                this.createDomWrapperIfNeeded(devEUI, location);

                // Загружаем его измерения
                try {
                    const rawData = await ApiClient.getMeasurements(devEUI);
                    
                    // Парсим время ISO 8601 (Instant из бэкенда) в локальное время браузера
                    const labels = rawData.map(item => {
                        const d = new Date(item.gatewayTime);
                        return d.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
                    });

                    // Собираем данные в массивы
                    const temperatures = rawData.map(item => item.temperature);
                    const humidities = rawData.map(item => item.humidity);

                    // Отдаем команду нарисовать/обновить график
                    ChartService.render(devEUI, labels, temperatures, humidities);

                } catch (e) {
                    console.log(`Пропуск оновлення для ${devEUI}:`, e.message);
                }
            }
        } catch (error) {
            console.error("Критична помилка синхронізації:", error);
        }
    },

    // Запуск приложения
    init() {
        this.syncData(); // Первый запуск сразу
        setInterval(() => this.syncData(), 5000); // Повторять каждые 5 секунд
    }
};

// Ждем, пока загрузится HTML, и запускаем "мозг"
document.addEventListener("DOMContentLoaded", () => {
    AppController.init();
});