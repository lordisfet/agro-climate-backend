const ApiClient = {
    async getNodes() {
        const response = await fetch('/api/v1/nodes');
        if (!response.ok) throw new Error('No nodes data');
        return response.json();
    },

    async getMeasurements(devEUI) {
        const response = await fetch(`/api/v1/measurements/node/${devEUI}`);
        if (!response.ok) throw new Error(`No measurements data for: ${devEUI}`);
        return response.json();
    }
};