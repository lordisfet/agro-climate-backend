const AppController = {
    container: document.getElementById('charts-container'),
    
    // НОВОЕ СОСТОЯНИЕ: Храним текущий выбранный интервал
    currentRange: '8H', 

    // Метод: Математика вычисления стартовой даты
    calculateStartDate(range) {
        if (range === 'MAX') return null; // Для MAX возвращаем null (сервер отдаст всё)

        const now = new Date(); // Берем текущее время браузера
        
        // Отнимаем нужное количество времени
        switch (range) {
            case '8H': now.setHours(now.getHours() - 8); break;
            case '1D': now.setDate(now.getDate() - 1); break;
            case '7D': now.setDate(now.getDate() - 7); break;
            case '1M': now.setMonth(now.getMonth() - 1); break;
            case '6M': now.setMonth(now.getMonth() - 6); break;
            case '1Y': now.setFullYear(now.getFullYear() - 1); break;
        }
        
        // Магия: toISOString() автоматически переводит время в UTC
        // и выдает строку формата "2026-06-17T08:00:00.000Z", 
        // которую идеально понимает твой Spring Boot бэкенд!
        return now.toISOString(); 
    },

    // Метод: Привязка событий к кнопкам
    setupEventListeners() {
        const buttons = document.querySelectorAll('.time-btn');
        buttons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                // Убираем класс active у всех кнопок
                buttons.forEach(b => b.classList.remove('active'));
                
                // Добавляем класс active нажатой кнопке
                e.target.classList.add('active');
                
                // Обновляем состояние
                this.currentRange = e.target.dataset.range;
                
                // МГНОВЕННО запрашиваем новые данные и перерисовываем графики!
                this.syncData(); 
            });
        });
    },

    createDomWrapperIfNeeded(devEUI, location) {
        if (!document.getElementById(`wrapper-${devEUI}`)) {
            const wrapper = document.createElement('div');
            wrapper.id = `wrapper-${devEUI}`;
            wrapper.className = 'chart-wrapper';
            wrapper.innerHTML = `
                <h3 class="chart-title">Location: ${location} <br><small style="color:gray; font-size: 0.8em;">ID: ${devEUI}</small></h3>
                <div class="canvas-container">
                    <canvas id="chart-${devEUI}"></canvas>
                </div>
            `;
            this.container.appendChild(wrapper);
        }
    },

    async syncData() {
        try {
            const nodes = await ApiClient.getNodes();
            
            if (nodes.length === 0) {
                this.container.innerHTML = '<p style="text-align:center; color:#666;">Активні датчики не знайдено.</p>';
                return;
            }

            // Вычисляем startDate ОДИН РАЗ для всех датчиков
            const startDateISO = this.calculateStartDate(this.currentRange);

            for (const node of nodes) {
                const devEUI = node.devEUI;
                const locationName = node.locationName || 'Невідома локація';

                this.createDomWrapperIfNeeded(devEUI, locationName);

                try {
                    // Передаем дату в API!
                    const rawData = await ApiClient.getMeasurements(devEUI, startDateISO);
                    
                    const labels = rawData.map(item => {
                        const d = new Date(item.gatewayTime);
                        
                        // Нюанс UX: если смотрим за год, показывать секунды глупо.
                        // Пока оставляем полный формат, но в будущем можно форматировать в зависимости от this.currentRange
                        return d.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
                    });

                    const temperatures = rawData.map(item => item.temperature);
                    const humidities = rawData.map(item => item.humidity);

                    ChartService.render(devEUI, labels, temperatures, humidities);

                } catch (e) {
                    console.log(`Пропуск оновлення для ${devEUI}:`, e.message);
                }
            }
        } catch (error) {
            console.error("Критична помилка синхронізації:", error);
        }
    },

    init() {
        this.setupEventListeners(); // Включаем слушатели кнопок
        this.syncData(); 
        setInterval(() => this.syncData(), 5000); 
    }
};

document.addEventListener("DOMContentLoaded", () => {
    AppController.init();
});