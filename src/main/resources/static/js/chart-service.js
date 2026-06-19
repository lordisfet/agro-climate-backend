const ChartService = {
    instances: {}, // Словарь для хранения графиков: { "devEUI": объектГрафика }

    // Метод отрисовки или обновления графика
    render(devEUI, labels, temperatures, humidities) {
        if (this.instances[devEUI]) {
            // Если график уже существует, просто подменяем массивы данных и обновляем
            this.instances[devEUI].data.labels = labels;
            this.instances[devEUI].data.datasets[0].data = temperatures;
            this.instances[devEUI].data.datasets[1].data = humidities;
            this.instances[devEUI].update();
        } else {
            // Если графика еще нет, создаем новый с нуля
            const ctx = document.getElementById(`chart-${devEUI}`).getContext('2d');
            this.instances[devEUI] = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        { label: 'Temperature (°C)', data: temperatures, borderColor: '#ff6384', backgroundColor: 'rgba(255, 99, 132, 0.1)', fill: true, tension: 0.3 },
                        { label: 'Humidity (%)', data: humidities, borderColor: '#36a2eb', backgroundColor: 'rgba(54, 162, 235, 0.1)', fill: true, yAxisID: 'y1', tension: 0.3 }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    animation: { duration: 800 },
                    interaction: { mode: 'index', intersect: false },
                    scales: {
                        y: { type: 'linear', position: 'left', title: { display: true, text: 'Temperature °C' } },
                        y1: { type: 'linear', position: 'right', grid: { drawOnChartArea: false }, title: { display: true, text: 'Humidity %' } }
                    }
                }
            });
        }
    }
};