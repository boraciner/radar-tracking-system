'use strict';

// ─── Canvas setup ────────────────────────────────────────────────────────────
const canvas   = document.getElementById('scope');
const ctx      = canvas.getContext('2d');
const W        = canvas.width;
const H        = canvas.height;
const MARGIN   = 40;
const PLOT_W   = W - 2 * MARGIN;
const PLOT_H   = H - 2 * MARGIN;
const DATA_MIN = 0;
const DATA_MAX = 11;   // coordinate space 0..10 with a little padding

// ─── Trail length ─────────────────────────────────────────────────────────────
const TRAIL_LEN = 30;

// ─── Colour palettes ─────────────────────────────────────────────────────────
// Target colours (ground truth + raw plots), keyed by targetId
const TARGET_COLORS = {
    1: '#f9e2af',  // yellow
    2: '#89dceb',  // cyan
    3: '#a6e3a1',  // green
    4: '#cba6f7',  // mauve
    5: '#fab387',  // peach
};
function targetColor(id) {
    return TARGET_COLORS[id] || '#cdd6f4';
}

// Track colours (Kalman output), cycling through a palette
const TRACK_PALETTE = [
    '#89b4fa', '#f38ba8', '#a6e3a1', '#fab387',
    '#cba6f7', '#89dceb', '#f9e2af', '#eba0ac',
];
function trackColor(id) {
    return TRACK_PALETTE[(id - 1) % TRACK_PALETTE.length];
}

// ─── Data stores ─────────────────────────────────────────────────────────────
// plotHistory[targetId]  = [{x, y}]  — noisy measurements
// truthHistory[targetId] = [{x, y}]  — true positions
// trackHistory[trackId]  = [{x, y}]  — Kalman estimates
const plotHistory  = {};
const truthHistory = {};
const trackHistory = {};

// latest track states for the side panel
const latestTracks = {};

let totalPlots = 0;

// ─── Visibility toggles ───────────────────────────────────────────────────────
let showPlots  = true;
let showTracks = true;
let showTruth  = true;

document.getElementById('chk-plots').addEventListener('change', e  => { showPlots  = e.target.checked; });
document.getElementById('chk-tracks').addEventListener('change', e => { showTracks = e.target.checked; });
document.getElementById('chk-truth').addEventListener('change', e  => { showTruth  = e.target.checked; });

// ─── Coordinate transform ─────────────────────────────────────────────────────
function cx(v) {
    return MARGIN + (v - DATA_MIN) / (DATA_MAX - DATA_MIN) * PLOT_W;
}
function cy(v) {
    // flip Y so (0,0) is bottom-left
    return H - MARGIN - (v - DATA_MIN) / (DATA_MAX - DATA_MIN) * PLOT_H;
}

// ─── History helpers ─────────────────────────────────────────────────────────
function push(store, key, point) {
    if (!store[key]) store[key] = [];
    store[key].push(point);
    if (store[key].length > TRAIL_LEN) store[key].shift();
}

// ─── Drawing primitives ───────────────────────────────────────────────────────
function drawGrid() {
    ctx.strokeStyle = '#1e1e2e';
    ctx.lineWidth   = 1;
    const steps = 10;
    for (let i = 0; i <= steps; i++) {
        const x = MARGIN + (i / steps) * PLOT_W;
        const y = MARGIN + (i / steps) * PLOT_H;
        ctx.beginPath(); ctx.moveTo(x, MARGIN); ctx.lineTo(x, H - MARGIN); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(MARGIN, y); ctx.lineTo(W - MARGIN, y); ctx.stroke();
    }
    // axis labels
    ctx.fillStyle   = '#45475a';
    ctx.font        = '10px Courier New';
    ctx.textAlign   = 'center';
    for (let i = 0; i <= steps; i++) {
        const val = DATA_MIN + (i / steps) * (DATA_MAX - DATA_MIN);
        ctx.fillText(val.toFixed(0), MARGIN + (i / steps) * PLOT_W, H - MARGIN + 14);
        ctx.fillText(val.toFixed(0), MARGIN - 16, cy(val) + 4);
    }
}

function drawTrail(history, color, alpha = 0.25) {
    if (history.length < 2) return;
    for (let i = 1; i < history.length; i++) {
        const progress = i / history.length;
        ctx.strokeStyle = color;
        ctx.globalAlpha = alpha * progress;
        ctx.lineWidth   = 1;
        ctx.beginPath();
        ctx.moveTo(cx(history[i - 1].x), cy(history[i - 1].y));
        ctx.lineTo(cx(history[i].x),     cy(history[i].y));
        ctx.stroke();
    }
    ctx.globalAlpha = 1;
}

function drawCross(x, y, color, size = 5) {
    ctx.strokeStyle = color;
    ctx.lineWidth   = 1.5;
    ctx.beginPath();
    ctx.moveTo(cx(x) - size, cy(y));
    ctx.lineTo(cx(x) + size, cy(y));
    ctx.moveTo(cx(x), cy(y) - size);
    ctx.lineTo(cx(x), cy(y) + size);
    ctx.stroke();
}

function drawCircle(x, y, color, r = 6) {
    ctx.strokeStyle = color;
    ctx.lineWidth   = 2;
    ctx.beginPath();
    ctx.arc(cx(x), cy(y), r, 0, Math.PI * 2);
    ctx.stroke();
    // filled centre dot
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(cx(x), cy(y), 2, 0, Math.PI * 2);
    ctx.fill();
}

function drawTriangle(x, y, color, size = 6) {
    const px = cx(x), py = cy(y);
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.moveTo(px, py - size);
    ctx.lineTo(px + size * 0.866, py + size * 0.5);
    ctx.lineTo(px - size * 0.866, py + size * 0.5);
    ctx.closePath();
    ctx.fill();
}

function drawTrackLabel(x, y, id, color) {
    ctx.fillStyle = color;
    ctx.font      = '10px Courier New';
    ctx.textAlign = 'left';
    ctx.fillText('T' + id, cx(x) + 8, cy(y) - 4);
}

// ─── Main render loop ─────────────────────────────────────────────────────────
function render() {
    ctx.clearRect(0, 0, W, H);

    // background
    ctx.fillStyle = '#050510';
    ctx.fillRect(0, 0, W, H);

    drawGrid();

    // Ground truth layer
    if (showTruth) {
        for (const [id, hist] of Object.entries(truthHistory)) {
            const color = targetColor(Number(id));
            drawTrail(hist, color, 0.2);
            if (hist.length > 0) {
                const last = hist[hist.length - 1];
                drawTriangle(last.x, last.y, color);
            }
        }
    }

    // Raw plots layer
    if (showPlots) {
        for (const [id, hist] of Object.entries(plotHistory)) {
            const color = targetColor(Number(id));
            drawTrail(hist, color, 0.15);
            if (hist.length > 0) {
                const last = hist[hist.length - 1];
                drawCross(last.x, last.y, color);
            }
        }
    }

    // Kalman track layer
    if (showTracks) {
        for (const [id, hist] of Object.entries(trackHistory)) {
            const color = trackColor(Number(id));
            drawTrail(hist, color, 0.35);
            if (hist.length > 0) {
                const last = hist[hist.length - 1];
                drawCircle(last.x, last.y, color);
                drawTrackLabel(last.x, last.y, id, color);
            }
        }
    }

    requestAnimationFrame(render);
}

// ─── Side panel updates ───────────────────────────────────────────────────────
function updateStats() {
    document.getElementById('stat-tracks').textContent = Object.keys(latestTracks).length;
    document.getElementById('stat-plots').textContent  = totalPlots;

    // track list
    const list = document.getElementById('track-list');
    list.innerHTML = '';
    for (const [id, t] of Object.entries(latestTracks)) {
        const color = trackColor(Number(id));
        list.innerHTML +=
            `<div style="margin-bottom:4px;">` +
            `<span style="color:${color};">■</span> ` +
            `T${id} &nbsp; x=${t.x.toFixed(2)} y=${t.y.toFixed(2)} ` +
            `vx=${t.vx.toFixed(2)} vy=${t.vy.toFixed(2)}` +
            `</div>`;
    }

    // target legend (built once when new targets are seen)
    const legend = document.getElementById('target-legend');
    legend.innerHTML = '';
    for (const id of Object.keys(truthHistory).sort()) {
        const color = targetColor(Number(id));
        legend.innerHTML +=
            `<div style="margin-bottom:4px;">` +
            `<span style="color:${color};">▲</span> Target ${id}` +
            `</div>`;
    }
}

// ─── WebSocket ────────────────────────────────────────────────────────────────
let ws;

function connect() {
    ws = new WebSocket('ws://localhost:8080/user');

    ws.onopen = () => {
        document.getElementById('status-badge').textContent  = 'CONNECTED';
        document.getElementById('status-badge').className    = 'badge bg-success';
    };

    ws.onclose = () => {
        document.getElementById('status-badge').textContent = 'DISCONNECTED';
        document.getElementById('status-badge').className   = 'badge bg-danger';
        setTimeout(connect, 3000);
    };

    ws.onerror = () => ws.close();

    ws.onmessage = (event) => {
        try {
            const msg = JSON.parse(event.data);
            if (msg.type === 'PLOT') {
                const p = msg.payload;
                push(plotHistory,  p.targetId, {x: p.x,     y: p.y});
                push(truthHistory, p.targetId, {x: p.trueX, y: p.trueY});
                totalPlots++;
            } else if (msg.type === 'TRACK') {
                const t = msg.payload;
                push(trackHistory, t.trackId, {x: t.x, y: t.y});
                latestTracks[t.trackId] = t;
            }
            updateStats();
        } catch (e) {
            console.warn('Bad message:', event.data, e);
        }
    };
}

// ─── Boot ─────────────────────────────────────────────────────────────────────
requestAnimationFrame(render);
connect();
