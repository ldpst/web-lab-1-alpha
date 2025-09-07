// graph.js
export class GraphDrawer {
    constructor(canvasId) {
        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');
        this.currentR = null;
        this.currentPoints = [];

        this.resizeCanvas()
        window.addEventListener("resize", () => this.resizeCanvas())
    }

    resizeCanvas() {
        this.canvas.width = this.canvas.clientWidth;
        this.canvas.height = this.canvas.clientHeight;

        this.center = {
            x: this.canvas.width / 2,
            y: this.canvas.height / 2
        };

        this.baseScale = Math.min(this.canvas.width, this.canvas.height) / 3;
        this.drawGraph(this.currentR);
    }

    clearCurrentPoints() {
        this.currentPoints = [];
    }

    setCurrentR(r) {
        this.currentR = r;
    }

    setCurrentPoints(points) {
        this.currentPoints = points;
    }

    drawGraph(r = null) {
        this.setCurrentR(r);
        const width = this.canvas.width;
        const height = this.canvas.height;
        const center = this.center;
        const baseScale = this.baseScale;
        const ctx = this.ctx;

        ctx.clearRect(0, 0, width, height);

        // Оси
        ctx.beginPath();
        ctx.strokeStyle = '#000';
        ctx.lineWidth = 3;

        ctx.moveTo(0, center.y);
        ctx.lineTo(width, center.y);

        ctx.moveTo(center.x, 0);
        ctx.lineTo(center.x, height);

        // Стрелки
        ctx.moveTo(width, center.y);
        ctx.lineTo(width - 25, center.y - 12);
        ctx.moveTo(width, center.y);
        ctx.lineTo(width - 25, center.y + 12);

        ctx.moveTo(center.x, 0);
        ctx.lineTo(center.x - 12, 25);
        ctx.moveTo(center.x, 0);
        ctx.lineTo(center.x + 12, 25);
        ctx.stroke();

        ctx.font = '20px Arial';
        ctx.fillStyle = '#000';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';

        // Метки R
        const displayR = (this.currentR !== null) ? this.currentR : 'R';
        const displayRHalf = (this.currentR !== null) ? (this.currentR / 2).toString() : 'R/2';
        const displayRNegHalf = (this.currentR !== null) ? (-this.currentR / 2).toString() : '-R/2';
        const displayRNeg = (this.currentR !== null) ? (-this.currentR).toString() : '-R';

        // Метки по X
        ctx.fillText(displayR, center.x + baseScale, center.y + 30);
        ctx.fillText(displayRHalf, center.x + baseScale / 2, center.y + 30);
        ctx.fillText(displayRNegHalf, center.x - baseScale / 2, center.y + 30);
        ctx.fillText(displayRNeg, center.x - baseScale, center.y + 30);

        // Метки по Y
        ctx.fillText(displayR, center.x - 30, center.y - baseScale);
        ctx.fillText(displayRHalf, center.x - 30, center.y - baseScale / 2);
        ctx.fillText(displayRNegHalf, center.x - 30, center.y + baseScale / 2);
        ctx.fillText(displayRNeg, center.x - 30, center.y + baseScale);

        // Фигуры - область
        ctx.fillStyle = 'rgba(97,166,90,0.5)';

        // Прямоугольник во I квадранте
        ctx.fillRect(
            center.x,
            center.y - baseScale,
            baseScale / 2,
            baseScale
        );

        // Треугольник во II квадранте
        ctx.beginPath();
        ctx.moveTo(center.x, center.y);
        ctx.lineTo(center.x - baseScale / 2, center.y);
        ctx.lineTo(center.x, center.y - baseScale / 2);
        ctx.closePath();
        ctx.fill();

        // Четверть круга в IV квадранте
        ctx.beginPath();
        ctx.moveTo(center.x, center.y);
        ctx.arc(center.x, center.y, baseScale, 0, Math.PI / 2, false);
        ctx.closePath();
        ctx.fill();

        // Перерисовываем текущие точки, если они есть
        this.currentPoints.forEach(point => {
            this.drawPoint(point.x, point.y, point.isHit);
        });
    }

    drawPoint(x, y, isHit) {
        if (this.currentR === null) {
            return;
        }

        const center = this.center;
        const baseScale = this.baseScale;
        const ctx = this.ctx;

        const scaledX = (x * baseScale) / this.currentR;
        const scaledY = (y * baseScale) / this.currentR;

        ctx.beginPath();
        ctx.arc(
            center.x + scaledX,
            center.y - scaledY,
            5,
            0,
            Math.PI * 2
        );
        ctx.fillStyle = isHit ? 'green' : 'red';
        ctx.fill();
    }

    clearDynamicElements() {
        this.drawGraph(this.currentR);
    }

    drawMouseLines(mouseX, mouseY, displayX, displayY) {
        this.clearDynamicElements();

        const ctx = this.ctx;
        const canvas = this.canvas;

        ctx.beginPath();
        ctx.strokeStyle = 'rgba(0, 0, 0, 0.5)';
        ctx.lineWidth = 1;

        ctx.moveTo(mouseX, 0);
        ctx.lineTo(mouseX, canvas.height);

        ctx.moveTo(0, mouseY);
        ctx.lineTo(canvas.width, mouseY);
        ctx.stroke();

        ctx.font = '14px Arial';
        ctx.fillStyle = '#000';
        ctx.textAlign = 'left';
        ctx.textBaseline = 'bottom';

        ctx.fillText(`X: ${displayX.toFixed(2)}`, mouseX + 10, mouseY - 10);
        ctx.fillText(`Y: ${displayY.toFixed(2)}`, mouseX + 10, mouseY + 20);

        this.currentPoints.forEach(point => {
            this.drawPoint(point.x, point.y, point.isHit);
        });
    }

    clearMouseLines() {
        this.clearDynamicElements();
    }

    canvasToGraphCoords(canvasX, canvasY) {
        const x = (canvasX - this.center.x) / this.baseScale * this.currentR;
        const y = (this.center.y - canvasY) / this.baseScale * this.currentR;
        return {x, y};
    }
}