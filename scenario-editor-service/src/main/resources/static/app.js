'use strict';

const canvas  = document.getElementById('editor');
const ctx     = canvas.getContext('2d');
const W       = canvas.width;
const H       = canvas.height;
const MARGIN  = 40;
const PLOT_W  = W - 2 * MARGIN;
const PLOT_H  = H - 2 * MARGIN;
const D_MIN   = 0;
const D_MAX   = 11;

const THREAT_API = 'http://localhost:8300';
const RADAR_API  = 'http://localhost:8000';

// ─── State ───────────────────────────────────────────────────────────────────
let zones      = [];
let asset      = { x: 5.0, y: 5.0 };
let ecmMode    = 'NONE';
let activeTool = 'asset';

// ─── Coordinate helpers ───────────────────────────────────────────────────────
function cx(v)  { return MARGIN + (v - D_MIN) / (D_MAX - D_MIN) * PLOT_W; }
function cy(v)  { return H - MARGIN - (v - D_MIN) / (D_MAX - D_MIN) * PLOT_H; }
function dataX(px) { return (px - MARGIN) / PLOT_W * (D_MAX - D_MIN) + D_MIN; }
function dataY(py) { return (H - MARGIN - py) / PLOT_H * (D_MAX - D_MIN) + D_MIN; }
function px2r(r)   { return r / (D_MAX - D_MIN) * PLOT_W; }

// ─── Tool controls ────────────────────────────────────────────────────────────
function setTool(tool) {
    activeTool = tool;
    document.getElementById('tool-asset').classList.toggle('active', tool === 'asset');
    document.getElementById('tool-zone').classList.toggle('active',  tool === 'zone');
    document.getElementById('zone-params').style.display = tool === 'zone' ? 'block' : 'none';
}

// ─── Canvas interaction ───────────────────────────────────────────────────────
canvas.addEventListener('mousemove', e => {
    const r = canvas.getBoundingClientRect();
    const x = dataX(e.clientX - r.left), y = dataY(e.clientY - r.top);
    if (x >= 0 && x <= 10 && y >= 0 && y <= 10) {
        document.getElementById('coord-display').textContent = `x: ${x.toFixed(2)}  y: ${y.toFixed(2)}`;
    }
});

canvas.addEventListener('click', e => {
    const r = canvas.getBoundingClientRect();
    const x = dataX(e.clientX - r.left), y = dataY(e.clientY - r.top);
    if (x < 0 || x > 10 || y < 0 || y > 10) return;

    if (activeTool === 'asset') placeAsset(x, y);
    else if (activeTool === 'zone') addZone(x, y);
});

// ─── Asset ────────────────────────────────────────────────────────────────────
async function placeAsset(x, y) {
    try {
        await fetch(`${THREAT_API}/asset`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ x, y })
        });
        asset = { x, y };
        document.getElementById('asset-pos').textContent = `★   x: ${x.toFixed(2)}   y: ${y.toFixed(2)}`;
    } catch(e) { console.warn('asset update failed', e); }
}

// ─── IFF Zones ────────────────────────────────────────────────────────────────
async function addZone(x, y) {
    const name   = document.getElementById('zone-name').value.trim() || `Zone-${zones.length + 1}`;
    const radius = parseFloat(document.getElementById('zone-radius').value) || 1.5;
    const type   = document.getElementById('zone-type').value;
    try {
        const resp = await fetch(`${THREAT_API}/iff-zones`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, centerX: x, centerY: y, radius, type })
        });
        const created = await resp.json();
        zones.push(created);
        renderZoneList();
        document.getElementById('zone-name').value = '';
    } catch(e) { console.warn('zone add failed', e); }
}

async function deleteZone(id) {
    try {
        await fetch(`${THREAT_API}/iff-zones/${id}`, { method: 'DELETE' });
        zones = zones.filter(z => z.id !== id);
        renderZoneList();
    } catch(e) { console.warn('zone delete failed', e); }
}

function renderZoneList() {
    const list = document.getElementById('zone-list');
    document.getElementById('zone-count').textContent = `(${zones.length})`;
    if (zones.length === 0) {
        list.innerHTML = '<div style="color:#585b70; font-size:11px;">No zones. Select "Add IFF Zone" and click canvas.</div>';
        return;
    }
    list.innerHTML = zones.map(z =>
        `<div class="zone-item">
           <span style="color:${zoneColor(z.type)}">■</span>
           &nbsp;<span>${z.name} &nbsp; <span style="color:#6c7086;">${z.type}</span>
           &nbsp; (${z.centerX.toFixed(1)}, ${z.centerY.toFixed(1)}) r=${z.radius}</span>
           <button class="btn-del" onclick="deleteZone('${z.id}')">✕</button>
         </div>`
    ).join('');
}

// ─── ECM ──────────────────────────────────────────────────────────────────────
async function applyEcm() {
    const mode = document.getElementById('ecm-mode').value;
    try {
        await fetch(`${RADAR_API}/ecm`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ mode })
        });
        ecmMode = mode;
        const badge = document.getElementById('ecm-active');
        badge.textContent = mode;
        badge.className   = `ecm-badge ecm-${mode}`;
    } catch(e) { console.warn('ECM update failed', e); }
}

// ─── Load initial state ───────────────────────────────────────────────────────
async function loadState() {
    try {
        const [zR, aR, eR] = await Promise.all([
            fetch(`${THREAT_API}/iff-zones`),
            fetch(`${THREAT_API}/asset`),
            fetch(`${RADAR_API}/ecm`)
        ]);
        zones   = await zR.json();
        asset   = await aR.json();
        const e = await eR.json();
        ecmMode = e.mode;
        document.getElementById('ecm-mode').value    = ecmMode;
        const badge = document.getElementById('ecm-active');
        badge.textContent = ecmMode;
        badge.className   = `ecm-badge ecm-${ecmMode}`;
        document.getElementById('asset-pos').textContent = `★   x: ${asset.x.toFixed(2)}   y: ${asset.y.toFixed(2)}`;
        document.getElementById('svc-status').textContent = 'ONLINE';
        document.getElementById('svc-status').className   = 'badge bg-success';
        renderZoneList();
    } catch(e) {
        document.getElementById('svc-status').textContent = 'SERVICES OFFLINE';
        document.getElementById('svc-status').className   = 'badge bg-danger';
        console.warn('Could not reach services:', e.message);
    }
}

// Refresh zones every 5 s in case another editor changed them
setInterval(async () => {
    try {
        zones = await (await fetch(`${THREAT_API}/iff-zones`)).json();
        renderZoneList();
    } catch(_) {}
}, 5000);

// ─── Drawing helpers ──────────────────────────────────────────────────────────
function zoneColor(type) {
    return { FRIENDLY: '#a6e3a1', RESTRICTED: '#f9e2af', HOSTILE: '#f38ba8' }[type] || '#cdd6f4';
}

function drawGrid() {
    ctx.strokeStyle = '#1e1e2e';
    ctx.lineWidth   = 1;
    for (let i = 0; i <= 10; i++) {
        const px = cx(i), py = cy(i);
        ctx.beginPath(); ctx.moveTo(px, MARGIN);    ctx.lineTo(px, H - MARGIN); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(MARGIN, py);    ctx.lineTo(W - MARGIN, py); ctx.stroke();
    }
    ctx.fillStyle = '#45475a';
    ctx.font      = '10px Courier New';
    ctx.textAlign = 'center';
    for (let i = 0; i <= 10; i++) {
        ctx.fillText(i, cx(i), H - MARGIN + 14);
        ctx.fillText(i, MARGIN - 16, cy(i) + 4);
    }
}

function drawSpotOverlay() {
    if (ecmMode !== 'SPOT') return;
    ctx.fillStyle   = '#f38ba8';
    ctx.globalAlpha = 0.07;
    ctx.fillRect(MARGIN, MARGIN, cx(4) - MARGIN, PLOT_H);
    ctx.globalAlpha = 1;
    ctx.strokeStyle = '#f38ba8';
    ctx.lineWidth   = 1.5;
    ctx.setLineDash([5, 5]);
    ctx.beginPath(); ctx.moveTo(cx(4), MARGIN); ctx.lineTo(cx(4), H - MARGIN); ctx.stroke();
    ctx.setLineDash([]);
    ctx.fillStyle   = '#f38ba8';
    ctx.font        = '10px Courier New';
    ctx.textAlign   = 'center';
    ctx.fillText('ECM BLANKED', (MARGIN + cx(4)) / 2, MARGIN + 16);
}

function drawZones() {
    for (const z of zones) {
        const color = zoneColor(z.type);
        const cxv = cx(z.centerX), cyv = cy(z.centerY);
        const r   = px2r(z.radius);
        ctx.fillStyle   = color;
        ctx.globalAlpha = 0.08;
        ctx.beginPath(); ctx.arc(cxv, cyv, r, 0, Math.PI * 2); ctx.fill();
        ctx.strokeStyle = color;
        ctx.globalAlpha = 0.7;
        ctx.lineWidth   = 1.5;
        ctx.setLineDash([4, 4]);
        ctx.beginPath(); ctx.arc(cxv, cyv, r, 0, Math.PI * 2); ctx.stroke();
        ctx.setLineDash([]);
        ctx.globalAlpha = 1;
        ctx.fillStyle   = color;
        ctx.font        = '10px Courier New';
        ctx.textAlign   = 'center';
        ctx.fillText(z.name, cxv, cyv - r - 5);
        ctx.fillText(z.type, cxv, cyv + 4);
    }
}

function drawAsset() {
    const x = cx(asset.x), y = cy(asset.y);
    ctx.strokeStyle = '#f9e2af';
    ctx.lineWidth   = 1.5;
    ctx.globalAlpha = 0.4;
    ctx.beginPath(); ctx.arc(x, y, 16, 0, Math.PI * 2); ctx.stroke();
    ctx.globalAlpha = 1;
    ctx.fillStyle   = '#f9e2af';
    ctx.font        = '18px Arial';
    ctx.textAlign   = 'center';
    ctx.fillText('★', x, y + 6);
    ctx.font        = '9px Courier New';
    ctx.fillText('ASSET', x, y + 20);
}

function drawEcmBadge() {
    if (ecmMode === 'NONE') return;
    const colors = { BARRAGE: '#f38ba8', SPOT: '#fab387', DRFM: '#cba6f7' };
    ctx.fillStyle   = '#11111b';
    ctx.globalAlpha = 0.85;
    ctx.fillRect(W - MARGIN - 110, MARGIN, 110, 20);
    ctx.globalAlpha = 1;
    ctx.fillStyle   = colors[ecmMode] || '#cdd6f4';
    ctx.font        = '10px Courier New';
    ctx.textAlign   = 'right';
    ctx.fillText(`ECM: ${ecmMode}`, W - MARGIN - 4, MARGIN + 13);
}

// ─── Render loop ──────────────────────────────────────────────────────────────
function render() {
    ctx.clearRect(0, 0, W, H);
    ctx.fillStyle = '#050510';
    ctx.fillRect(0, 0, W, H);
    drawGrid();
    drawSpotOverlay();
    drawZones();
    drawAsset();
    drawEcmBadge();
    requestAnimationFrame(render);
}

loadState();
requestAnimationFrame(render);
