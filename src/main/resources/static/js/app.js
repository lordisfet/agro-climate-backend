const AppController = {
    container: document.getElementById('charts-container'),
    
    currentRange: '8H', 

    calculateStartDate(range) {
        if (range === 'MAX') return null;

        const now = new Date(); 
        switch (range) {
            case '8H': now.setHours(now.getHours() - 8); break;
            case '1D': now.setDate(now.getDate() - 1); break;
            case '7D': now.setDate(now.getDate() - 7); break;
            case '1M': now.setMonth(now.getMonth() - 1); break;
            case '6M': now.setMonth(now.getMonth() - 6); break;
            case '1Y': now.setFullYear(now.getFullYear() - 1); break;
        }
        
        return now.toISOString(); 
    },

    setupEventListeners() {
        const buttons = document.querySelectorAll('.time-btn');
        buttons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                buttons.forEach(b => b.classList.remove('active'));

                e.target.classList.add('active');

                this.currentRange = e.target.dataset.range;

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

            const startDateISO = this.calculateStartDate(this.currentRange);

            for (const node of nodes) {
                const devEUI = node.devEUI;
                const locationName = node.locationName || 'Невідома локація';

                this.createDomWrapperIfNeeded(devEUI, locationName);

                try {
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
        this.setupEventListeners();
        this.syncData(); 
        setInterval(() => this.syncData(), 5000); 
    }
};

document.addEventListener("DOMContentLoaded", () => {
    AppController.init();
});