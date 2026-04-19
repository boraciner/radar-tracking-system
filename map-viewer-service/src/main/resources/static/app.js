'use strict';

// ─── Canvas setup ─────────────────────────────────────────────────────────────
const canvas  = document.getElementById('scope');
const ctx     = canvas.getContext('2d');
const W       = canvas.width;
const H       = canvas.height;
const MARGIN  = 40;
const PLOT_W  = W - 2 * MARGIN;
const PLOT_H  = H - 2 * MARGIN;
const D_MIN   = 0;
const D_MAX   = 11;

const TRAIL_LEN = 30;

// ─── Colour palettes ──────────────────────────────────────────────────────────
const TARGET_COLORS = { 1: '#f9e2af', 2: '#89dceb', 3: '#a6e3a1', 4: '#cba6f7', 5: '#fab387' };
function targetColor(id) { return TARGET_COLORS[id] || '#cdd6f4'; }

const TRACK_PALETTE = ['#89b4fa','#f38ba8','#a6e3a1','#fab387','#cba6f7','#89dceb','#f9e2af','#eba0ac'];
function trackPaletteColor(id) { return TRACK_PALETTE[(id - 1) % TRACK_PALETTE.length]; }

const THREAT_COLORS = {
    FRIENDLY : '#a6e3a1',
    NEUTRAL  : '#cdd6f4',
    UNKNOWN  : '#f9e2af',
    SUSPECT  : '#fab387',
    HOSTILE  : '#f38ba8'
};
function threatColor(level) { return THREAT_COLORS[level] || '#cdd6f4'; }

const ZONE_COLORS = { FRIENDLY: '#a6e3a1', RESTRICTED: '#f9e2af', HOSTILE: '#f38ba8' };
function zoneColor(type) { return ZONE_COLORS[type] || '#cdd6f4'; }

function trackColor(id) {
    const t = threatData[id];
    return t ? threatColor(t.level) : trackPaletteColor(id);
}

// ─── Data stores ──────────────────────────────────────────────────────────────
const plotHistory  = {};
const truthHistory = {};
const trackHistory = {};
const latestTracks = {};
const threatData   = {};
let   zones        = [];
let   asset        = { x: 5.0, y: 5.0 };
let   ecmMode      = 'NONE';
let   totalPlots   = 0;

// ─── Visibility toggles ───────────────────────────────────────────────────────
let showPlots  = true;
let showTracks = true;
let showTruth  = true;
let showZones  = true;
let showAsset  = true;

document.getElementById('chk-plots') .addEventListener('change', e => { showPlots  = e.target.checked; });
document.getElementById('chk-tracks').addEventListener('change', e => { showTracks = e.target.checked; });
document.getElementById('chk-truth') .addEventListener('change', e => { showTruth  = e.target.checked; });
document.getElementById('chk-zones') .addEventListener('change', e => { showZones  = e.target.checked; });
document.getElementById('chk-asset') .addEventListener('change', e => { showAsset  = e.target.checked; });

// ─── Coordinate transforms ────────────────────────────────────────────────────
function cx(v) { return MARGIN + (v - D_MIN) / (D_MAX - D_MIN) * PLOT_W; }
function cy(v) { return H - MARGIN - (v - D_MIN) / (D_MAX - D_MIN) * PLOT_H; }
function px2r(r) { return r / (D_MAX - D_MIN) * PLOT_W; }

function push(store, key, point) {
    if (!store[key]) store[key] = [];
    store[key].push(point);
    if (store[key].length > TRAIL_LEN) store[key].shift();
}

// ─── Drawing helpers ──────────────────────────────────────────────────────────
function drawGrid() {
    ctx.strokeStyle = '#1e1e2e';
    ctx.lineWidth   = 1;
    for (let i = 0; i <= 10; i++) {
        const px = cx(i), py = cy(i);
        ctx.beginPath(); ctx.moveTo(px, MARGIN);   ctx.lineTo(px, H - MARGIN); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(MARGIN, py);   ctx.lineTo(W - MARGIN, py); ctx.stroke();
    }
    ctx.fillStyle = '#45475a';
    ctx.font      = '10px Courier New';
    ctx.textAlign = 'center';
    for (let i = 0; i <= 10; i++) {
        ctx.fillText(i, cx(i), H - MARGIN + 14);
        ctx.fillText(i, MARGIN - 16, cy(i) + 4);
    }
}

function drawTrail(history, color, alpha = 0.25) {
    if (history.length < 2) return;
    for (let i = 1; i < history.length; i++) {
        const p = i / history.length;
        ctx.strokeStyle = color;
        ctx.globalAlpha = alpha * p;
        ctx.lineWidth   = 1;
        ctx.beginPath();
        ctx.moveTo(cx(history[i-1].x), cy(history[i-1].y));
        ctx.lineTo(cx(history[i].x),   cy(history[i].y));
        ctx.stroke();
    }
    ctx.globalAlpha = 1;
}

function drawCross(x, y, color, size = 5) {
    ctx.strokeStyle = color; ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.moveTo(cx(x)-size, cy(y)); ctx.lineTo(cx(x)+size, cy(y));
    ctx.moveTo(cx(x), cy(y)-size); ctx.lineTo(cx(x), cy(y)+size);
    ctx.stroke();
}

function drawCircle(x, y, color, r = 6) {
    ctx.strokeStyle = color; ctx.lineWidth = 2;
    ctx.beginPath(); ctx.arc(cx(x), cy(y), r, 0, Math.PI*2); ctx.stroke();
    ctx.fillStyle = color;
    ctx.beginPath(); ctx.arc(cx(x), cy(y), 2, 0, Math.PI*2); ctx.fill();
}

function drawTriangle(x, y, color, size = 6) {
    const px = cx(x), py = cy(y);
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.moveTo(px, py - size);
    ctx.lineTo(px + size * 0.866, py + size * 0.5);
    ctx.lineTo(px - size * 0.866, py + size * 0.5);
    ctx.closePath(); ctx.fill();
}

function drawTrackLabel(x, y, id, color) {
    ctx.fillStyle = color; ctx.font = '10px Courier New'; ctx.textAlign = 'left';
    ctx.fillText('T' + id, cx(x) + 8, cy(y) - 4);
}

function drawIffZones() {
    for (const z of zones) {
        const color = zoneColor(z.type);
        const cxv = cx(z.centerX), cyv = cy(z.centerY);
        const r   = px2r(z.radius);
        ctx.fillStyle   = color;
        ctx.globalAlpha = 0.07;
        ctx.beginPath(); ctx.arc(cxv, cyv, r, 0, Math.PI*2); ctx.fill();
        ctx.strokeStyle = color;
        ctx.globalAlpha = 0.55;
        ctx.lineWidth   = 1.5;
        ctx.setLineDash([4, 4]);
        ctx.beginPath(); ctx.arc(cxv, cyv, r, 0, Math.PI*2); ctx.stroke();
        ctx.setLineDash([]);
        ctx.globalAlpha = 1;
        ctx.fillStyle   = color;
        ctx.font        = '10px Courier New';
        ctx.textAlign   = 'center';
        ctx.fillText(z.name, cxv, cyv - r - 4);
    }
}

function drawAssetMarker() {
    const x = cx(asset.x), y = cy(asset.y);
    ctx.strokeStyle = '#f9e2af'; ctx.lineWidth = 1.5;
    ctx.globalAlpha = 0.4;
    ctx.beginPath(); ctx.arc(x, y, 14, 0, Math.PI*2); ctx.stroke();
    ctx.globalAlpha = 1;
    ctx.fillStyle   = '#f9e2af';
    ctx.font        = '16px Arial'; ctx.textAlign = 'center';
    ctx.fillText('★', x, y + 6);
    ctx.font        = '9px Courier New';
    ctx.fillText('ASSET', x, y + 20);
}

function drawEcmOverlay() {
    if (ecmMode !== 'SPOT') return;
    ctx.fillStyle   = '#f38ba8'; ctx.globalAlpha = 0.06;
    ctx.fillRect(MARGIN, MARGIN, cx(4) - MARGIN, PLOT_H);
    ctx.globalAlpha = 1;
    ctx.strokeStyle = '#f38ba8'; ctx.lineWidth = 1.5;
    ctx.setLineDash([5, 5]);
    ctx.beginPath(); ctx.moveTo(cx(4), MARGIN); ctx.lineTo(cx(4), H - MARGIN); ctx.stroke();
    ctx.setLineDash([]);
    ctx.fillStyle   = '#f38ba8';
    ctx.font        = '9px Courier New'; ctx.textAlign = 'center';
    ctx.fillText('ECM SPOT', (MARGIN + cx(4)) / 2, MARGIN + 14);
}

function drawEcmBadge() {
    if (ecmMode === 'NONE') return;
    const colors = { BARRAGE: '#f38ba8', SPOT: '#fab387', DRFM: '#cba6f7' };
    ctx.fillStyle   = '#11111b'; ctx.globalAlpha = 0.85;
    ctx.fillRect(W - MARGIN - 108, MARGIN, 108, 18);
    ctx.globalAlpha = 1;
    ctx.fillStyle   = colors[ecmMode] || '#cdd6f4';
    ctx.font        = '9px Courier New'; ctx.textAlign = 'right';
    ctx.fillText(`ECM: ${ecmMode}`, W - MARGIN - 4, MARGIN + 12);
}

// ─── Main render loop ─────────────────────────────────────────────────────────
function render() {
    ctx.clearRect(0, 0, W, H);
    ctx.fillStyle = '#050510'; ctx.fillRect(0, 0, W, H);

    drawGrid();
    drawEcmOverlay();
    if (showZones)  drawIffZones();
    if (showAsset)  drawAssetMarker();

    if (showTruth) {
        for (const [id, hist] of Object.entries(truthHistory)) {
            const color = targetColor(Number(id));
            drawTrail(hist, color, 0.2);
            if (hist.length > 0) drawTriangle(hist[hist.length-1].x, hist[hist.length-1].y, color);
        }
    }

    if (showPlots) {
        for (const [id, hist] of Object.entries(plotHistory)) {
            const color = targetColor(Number(id));
            drawTrail(hist, color, 0.15);
            if (hist.length > 0) drawCross(hist[hist.length-1].x, hist[hist.length-1].y, color);
        }
    }

    if (showTracks) {
        for (const [id, hist] of Object.entries(trackHistory)) {
            const color = trackColor(Number(id));
            drawTrail(hist, color, 0.35);
            if (hist.length > 0) {
                const last = hist[hist.length-1];
                drawCircle(last.x, last.y, color);
                drawTrackLabel(last.x, last.y, id, color);
            }
        }
    }

    drawEcmBadge();
    requestAnimationFrame(render);
}

// ─── Side panel ───────────────────────────────────────────────────────────────
function updateStats() {
    document.getElementById('stat-tracks').textContent = Object.keys(latestTracks).length;
    document.getElementById('stat-plots').textContent  = totalPlots;

    // Threat counts
    const counts = { FRIENDLY: 0, NEUTRAL: 0, UNKNOWN: 0, SUSPECT: 0, HOSTILE: 0 };
    for (const ta of Object.values(threatData)) {
        if (counts[ta.level] !== undefined) counts[ta.level]++;
    }
    for (const [lvl, n] of Object.entries(counts)) {
        document.getElementById(`cnt-${lvl}`).textContent = n;
    }

    // Track list
    const list = document.getElementById('track-list');
    list.innerHTML = '';
    for (const [id, t] of Object.entries(latestTracks)) {
        const color = trackColor(Number(id));
        const ta    = threatData[id];
        const lvl   = ta ? ta.level : '—';
        const dist  = ta ? ` d=${ta.distanceToAsset.toFixed(1)}` : '';
        list.innerHTML +=
            `<div style="margin-bottom:3px;">` +
            `<span style="color:${color};">■</span> ` +
            `T${id} <span style="color:${color}; font-size:10px;">${lvl}</span>` +
            ` x=${t.x.toFixed(1)} y=${t.y.toFixed(1)}${dist}` +
            `</div>`;
    }

    // Target legend
    const legend = document.getElementById('target-legend');
    legend.innerHTML = '';
    for (const id of Object.keys(truthHistory).sort()) {
        const color = targetColor(Number(id));
        legend.innerHTML += `<div style="margin-bottom:3px;"><span style="color:${color};">▲</span> Target ${id}</div>`;
    }

    // IFF zones panel
    const zp = document.getElementById('zone-list-panel');
    if (zones.length === 0) {
        zp.innerHTML = '<span style="color:#585b70; font-size:11px;">No zones — use Scenario Editor</span>';
    } else {
        zp.innerHTML = zones.map(z =>
            `<div style="margin-bottom:2px;"><span style="color:${zoneColor(z.type)};">■</span> ${z.name} <span style="color:#6c7086;">${z.type}</span></div>`
        ).join('');
    }
}

// ─── Remote state fetch ───────────────────────────────────────────────────────
async function refreshRemoteState() {
    try {
        zones = await (await fetch('http://localhost:8300/iff-zones')).json();
    } catch(_) {}
    try {
        asset = await (await fetch('http://localhost:8300/asset')).json();
    } catch(_) {}
    try {
        const e = await (await fetch('http://localhost:8000/ecm')).json();
        ecmMode = e.mode;
        const badge = document.getElementById('ecm-badge');
        badge.textContent = `ECM: ${ecmMode}`;
        badge.className   = ecmMode === 'NONE'
            ? 'badge bg-secondary'
            : 'badge bg-danger';
    } catch(_) {}
}

setInterval(refreshRemoteState, 5000);

// ─── WebSocket ────────────────────────────────────────────────────────────────
let ws;

function connect() {
    ws = new WebSocket('ws://localhost:8080/user');

    ws.onopen = () => {
        document.getElementById('status-badge').textContent = 'CONNECTED';
        document.getElementById('status-badge').className   = 'badge bg-success';
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
                push(plotHistory,  p.targetId, { x: p.x,     y: p.y });
                push(truthHistory, p.targetId, { x: p.trueX, y: p.trueY });
                totalPlots++;
            } else if (msg.type === 'TRACK') {
                const t = msg.payload;
                push(trackHistory, t.trackId, { x: t.x, y: t.y });
                latestTracks[t.trackId] = t;
            } else if (msg.type === 'THREAT') {
                const ta = msg.payload;
                threatData[ta.trackId] = ta;
            }
            updateStats();
        } catch(e) {
            console.warn('Bad WS message', event.data, e);
        }
    };
}

// ─── Boot ─────────────────────────────────────────────────────────────────────
refreshRemoteState();
requestAnimationFrame(render);
connect();
