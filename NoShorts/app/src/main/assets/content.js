// Redirect away from direct /shorts/ URLs
if (location.pathname.startsWith('/shorts')) {
  location.replace('/');
}

const SHORTS_SELECTORS = [
  'ytd-rich-shelf-renderer[is-shorts]',
  'ytd-reel-shelf-renderer',
  'ytd-reel-item-renderer',
  'ytd-guide-entry-renderer:has(a[href="/shorts"])',
  'ytd-guide-entry-renderer:has(a[href^="/shorts"])',
  'ytd-guide-entry-renderer:has([title="Shorts"])',
  'ytd-mini-guide-entry-renderer:has(a[href="/shorts"])',
  'ytd-mini-guide-entry-renderer:has(a[href^="/shorts"])',
  'ytd-mini-guide-entry-renderer:has([title="Shorts"])',
  'ytd-shelf-renderer:has(ytd-reel-item-renderer)',
  'ytd-video-renderer:has(a[href*="/shorts/"])',
  'ytd-compact-video-renderer:has(a[href*="/shorts/"])',
  'grid-shelf-view-model:has(ytm-shorts-lockup-view-model)',
  'ytm-shorts-lockup-view-model-v2',
  'ytm-shorts-lockup-view-model',
  'yt-chip-cloud-chip-renderer:has(a[href*="shorts"])',
  'ytm-pivot-bar-item-renderer:has(a[href^="/shorts"])',
  'ytm-pivot-bar-item-renderer:has([aria-label*="Shorts"])',
  'yt-tab-shape:has(a[href*="/shorts"])',
  'ytd-tab-renderer:has(a[href*="/shorts"])',
  '.pivot-bar-item-tab.pivot-shorts',
];

function hideShorts() {
  SHORTS_SELECTORS.forEach(selector => {
    document.querySelectorAll(selector).forEach(el => {
      el.style.setProperty('display', 'none', 'important');
    });
  });
}

hideShorts();

const observer = new MutationObserver(hideShorts);
observer.observe(document.body, { childList: true, subtree: true });