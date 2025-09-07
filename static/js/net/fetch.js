import {GraphDrawer} from "./graph.js";

document.addEventListener('DOMContentLoaded', async () => {
    const graphDrawer = new GraphDrawer('graphCanvas');
    graphDrawer.drawGraph();
});
