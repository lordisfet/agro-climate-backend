const ApiClient = {
    async getNodes() {
        const response = await fetch('/api/v1/nodes');
        if (!response.ok) throw new Error('No nodes data');
        return response.json();
    },

    async getMeasurements(devEUI, startTime = null, endTime = null) {
        let url = `/api/v1/measurements/node/${devEUI}`
        const param = new URLSearchParams();

        if (startTime) param.append('startDate', startTime);
        if (endTime) param.append('endDate', endTime);
        
        const queryString = param.toString();
        if (param) {
            url += `?${queryString}`
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error(`No measurements data for: ${devEUI}`);
        return response.json();
    }
};