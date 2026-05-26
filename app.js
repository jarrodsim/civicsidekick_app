// ============================================


// Civic Sidekick - Main Application JavaScript
// ============================================

// --- State ---
const state = {
  currentScreen: 'landing',
  address: '',
  reps: [],
  trackedBills: [],
  allBills: [],
  currentPage: 1,
  itemsPerPage: 10,
  currentBillFilter: ''
};

// ============================================
// GLOBAL HELPERS
// ============================================

function showLoading() {
  let overlay = document.getElementById('global-loading');
  if (!overlay) {
    overlay = document.createElement('div');
    overlay.id = 'global-loading';
    overlay.style.cssText = 'position:fixed;inset:0;background:rgba(255,255,255,0.7);z-index:9999;display:flex;align-items:center;justify-content:center;flex-direction:column;gap:12px';
    overlay.innerHTML = '<div style="width:36px;height:36px;border:3px solid #e2e4e8;border-top-color:#001f45;border-radius:50%;animation:spin 0.8s linear infinite"></div><div style="font-size:13px;color:#6b707b;font-weight:500">Loading...</div>';
    document.body.appendChild(overlay);
  }
  overlay.style.display = 'flex';
}

function hideLoading() {
  const overlay = document.getElementById('global-loading');
  if (overlay) overlay.style.display = 'none';
}

function showError(msg) {
  hideLoading();
  let banner = document.getElementById('error-banner');
  if (!banner) {
    banner = document.createElement('div');
    banner.id = 'error-banner';
    banner.style.cssText = 'position:fixed;top:0;left:0;right:0;z-index:9998;background:#c73a3a;color:white;padding:12px 20px;font-size:13px;font-weight:500;text-align:center;display:none';
    document.body.prepend(banner);
  }
  banner.textContent = msg;
  banner.style.display = 'block';
  clearTimeout(banner._hideTimer);
  banner._hideTimer = setTimeout(() => { banner.style.display = 'none'; }, 5000);
}

function hideError() {
  const banner = document.getElementById('error-banner');
  if (banner) banner.style.display = 'none';
}

// ============================================
// API CONFIG
// ============================================

// GovTrack.us API is public (no key needed for basic usage)
// Congress.gov API key reserved for future server-side use
const CONGRESS_GOV_API_KEY = '8y0kxMPAyL6mkWQJMb0CwaHZijyiorrb7WT4dPaS';

// OpenStates API key for state-level legislators
const OPENSTATES_API_KEY = '14758350-d06d-4c26-9ec0-cd8060abebd7';

// ============================================
// DATA / API FUNCTIONS
// ============================================

// --- GovTrack.us API ---
// Fetches recent bills (CORS-friendly, works from browser)
async function fetchBills() {
  if (state.allBills.length) return state.allBills;

  showLoading();

  try {
    // Fetch current active bills from GovTrack.us
    // GovTrack API is CORS-friendly and free (no key needed for basic usage)
    const limit = 50;
    const url = `https://www.govtrack.us/api/v2/bill?format=json&limit=${limit}&order_by=-current_status_date`;

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`GovTrack API error: ${response.status}`);
    }

    const data = await response.json();
    const billsData = data.objects || [];

    // Normalize bill data
    const bills = [];
    const topicKeywords = {
      'education': 'Education',
      'health': 'Healthcare',
      'tax': 'Tax Reform',
      'climate': 'Climate',
      'immigration': 'Immigration',
      'veteran': 'Veterans',
      'technology': 'Technology',
      'agriculture': 'Agriculture',
      'transportation': 'Transportation',
      'housing': 'Housing',
      'defense': 'Defense',
      'energy': 'Energy',
      'crime': 'Criminal Justice',
      'budget': 'Budget',
      'economy': 'Economy'
    };

    for (const b of billsData) {
      // Determine topic from bill title/keywords
      const titleLower = (b.title_without_number || b.title || '').toLowerCase();
      let topic = 'General';
      for (const [keyword, topicName] of Object.entries(topicKeywords)) {
        if (titleLower.includes(keyword)) {
          topic = topicName;
          break;
        }
      }

      // Determine status from GovTrack's normalized status
      let status = b.current_status_label || 'Introduced';

      // Bill type prefix
      const billType = b.bill_type_label || (b.bill_type === 'house_bill' ? 'H.R.' : b.bill_type === 'senate_bill' ? 'S.' : 'H.R.');
      const billNumber = b.number || '';

      // Sponsor
      const sponsor = b.sponsor ? b.sponsor.name || 'Unknown' : 'Unknown';

      // Summary - use title as fallback
      let summary = b.title_without_number || b.title || '';
      if (summary.length > 300) summary = summary.substring(0, 300) + '...';

      // Origin date
      const introducedDate = b.introduced_date || '';

      // Origin chamber
      const originChamber = b.bill_type && b.bill_type.includes('senate') ? 'Senate' : 'House';

      bills.push({
        id: b.display_number || `${billType} ${billNumber}`,
        title: b.title_without_number || b.title || `${billType} ${billNumber}`,
        summary: summary,
        sponsor: sponsor,
        status: status,
        introduced: introducedDate,
        topic: topic,
        congressDotGovUrl: b.link || `https://www.govtrack.us/congress/bills/118/${b.bill_type?.includes('senate') ? 's' : 'hr'}${billNumber}`,
        originChamber: originChamber
      });
    }

    hideLoading();
    state.allBills = bills;
    return bills;

  } catch (err) {
    hideLoading();
    console.error('GovTrack API error:', err);
    showError('Could not load bills. Using sample data as fallback.');
    // Fallback to sample data on error
    return fetchBillsFallback();
  }
}

// Fallback sample data if API fails
async function fetchBillsFallback() {
  if (state.allBills.length) return state.allBills;
  const topics = ['Education','Healthcare','Tax Reform','Climate','Immigration','Veterans','Technology','Agriculture','Transportation','Housing'];
  const statuses = ['Introduced','In Committee','Passed House','Passed Senate','Signed into Law'];
  const bills = [];
  for (let i = 1; i <= 50; i++) {
    const t = topics[i % topics.length];
    bills.push({
      id: `HR-${100 + i}`,
      title: `${t} Improvement Act of 2025`,
      summary: `This bill aims to improve ${t.toLowerCase()} systems across the United States.`,
      sponsor: `Rep. Sample ${String.fromCharCode(65 + (i % 26))}`,
      status: statuses[i % statuses.length],
      introduced: `2025-${String(Math.floor(i/2)+1).padStart(2,'0')}-${String((i*3)%28+1).padStart(2,'0')}`,
      topic: t,
      congressDotGovUrl: ''
    });
  }
  state.allBills = bills;
  return bills;
}

// --- Static Governor Lookup by State ---
// Updated as elections occur; covers all 50 states + DC
const STATE_GOVERNORS = {
  AL: { name: 'Kay Ivey', party: 'Republican' },
  AK: { name: 'Mike Dunleavy', party: 'Republican' },
  AZ: { name: 'Katie Hobbs', party: 'Democratic' },
  AR: { name: 'Sarah Huckabee Sanders', party: 'Republican' },
  CA: { name: 'Gavin Newsom', party: 'Democratic' },
  CO: { name: 'Jared Polis', party: 'Democratic' },
  CT: { name: 'Ned Lamont', party: 'Democratic' },
  DE: { name: 'Matt Meyer', party: 'Democratic' },
  FL: { name: 'Ron DeSantis', party: 'Republican' },
  GA: { name: 'Brian Kemp', party: 'Republican' },
  HI: { name: 'Josh Green', party: 'Democratic' },
  ID: { name: 'Brad Little', party: 'Republican' },
  IL: { name: 'JB Pritzker', party: 'Democratic' },
  IN: { name: 'Mike Braun', party: 'Republican' },
  IA: { name: 'Kim Reynolds', party: 'Republican' },
  KS: { name: 'Laura Kelly', party: 'Democratic' },
  KY: { name: 'Andy Beshear', party: 'Democratic' },
  LA: { name: 'Jeff Landry', party: 'Republican' },
  ME: { name: 'Janet Mills', party: 'Democratic' },
  MD: { name: 'Wes Moore', party: 'Democratic' },
  MA: { name: 'Maura Healey', party: 'Democratic' },
  MI: { name: 'Gretchen Whitmer', party: 'Democratic' },
  MN: { name: 'Tim Walz', party: 'Democratic' },
  MS: { name: 'Tate Reeves', party: 'Republican' },
  MO: { name: 'Mike Kehoe', party: 'Republican' },
  MT: { name: 'Greg Gianforte', party: 'Republican' },
  NE: { name: 'Jim Pillen', party: 'Republican' },
  NV: { name: 'Joe Lombardo', party: 'Republican' },
  NH: { name: 'Kelly Ayotte', party: 'Republican' },
  NJ: { name: 'Phil Murphy', party: 'Democratic' },
  NM: { name: 'Michelle Lujan Grisham', party: 'Democratic' },
  NY: { name: 'Kathy Hochul', party: 'Democratic' },
  NC: { name: 'Josh Stein', party: 'Democratic' },
  ND: { name: 'Kelly Armstrong', party: 'Republican' },
  OH: { name: 'Mike DeWine', party: 'Republican' },
  OK: { name: 'Kevin Stitt', party: 'Republican' },
  OR: { name: 'Tina Kotek', party: 'Democratic' },
  PA: { name: 'Josh Shapiro', party: 'Democratic' },
  RI: { name: 'Dan McKee', party: 'Democratic' },
  SC: { name: 'Henry McMaster', party: 'Republican' },
  SD: { name: 'Larry Rhoden', party: 'Republican' },
  TN: { name: 'Bill Lee', party: 'Republican' },
  TX: { name: 'Greg Abbott', party: 'Republican' },
  UT: { name: 'Spencer Cox', party: 'Republican' },
  VT: { name: 'Phil Scott', party: 'Republican' },
  VA: { name: 'Glenn Youngkin', party: 'Republican' },
  WA: { name: 'Bob Ferguson', party: 'Democratic' },
  WV: { name: 'Patrick Morrisey', party: 'Republican' },
  WI: { name: 'Tony Evers', party: 'Democratic' },
  WY: { name: 'Mark Gordon', party: 'Republican' },
  DC: { name: 'Muriel Bowser', party: 'Democratic' }
};

// State name → abbreviation mapping for OpenStates
const STATE_TO_ABBR = {
  'alabama': 'AL', 'alaska': 'AK', 'arizona': 'AZ', 'arkansas': 'AR',
  'california': 'CA', 'colorado': 'CO', 'connecticut': 'CT', 'delaware': 'DE',
  'florida': 'FL', 'georgia': 'GA', 'hawaii': 'HI', 'idaho': 'ID',
  'illinois': 'IL', 'indiana': 'IN', 'iowa': 'IA', 'kansas': 'KS',
  'kentucky': 'KY', 'louisiana': 'LA', 'maine': 'ME', 'maryland': 'MD',
  'massachusetts': 'MA', 'michigan': 'MI', 'minnesota': 'MN', 'mississippi': 'MS',
  'missouri': 'MO', 'montana': 'MT', 'nebraska': 'NE', 'nevada': 'NV',
  'new hampshire': 'NH', 'new jersey': 'NJ', 'new mexico': 'NM',
  'new york': 'NY', 'north carolina': 'NC', 'north dakota': 'ND',
  'ohio': 'OH', 'oklahoma': 'OK', 'oregon': 'OR', 'pennsylvania': 'PA',
  'rhode island': 'RI', 'south carolina': 'SC', 'south dakota': 'SD',
  'tennessee': 'TN', 'texas': 'TX', 'utah': 'UT', 'vermont': 'VT',
  'virginia': 'VA', 'washington': 'WA', 'west virginia': 'WV',
  'wisconsin': 'WI', 'wyoming': 'WY', 'district of columbia': 'DC'
};

// --- ZIP Code to State lookup via Zippopotam.us (free, CORS-enabled) ---
async function zipToState(zip) {
  try {
    const res = await fetch(`http://api.zippopotam.us/us/${zip}`);
    if (!res.ok) throw new Error('ZIP lookup failed');
    const data = await res.json();
    // Get the state abbreviation from the first place
    return data.places?.[0]?.['state abbreviation'] || null;
  } catch (err) {
    console.error('ZIP lookup error:', err);
    return null;
  }
}

// --- GovTrack.us API ---
// Finds current federal elected officials by state (CORS-friendly, no key needed)
async function fetchFederalRepsByState(stateCode) {
  try {
    const url = `https://www.govtrack.us/api/v2/role?format=json&current=true&state=${stateCode}&limit=60`;

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`GovTrack API error: ${response.status}`);
    }

    const data = await response.json();
    const roles = data.objects || [];

    const reps = [];

    for (const role of roles) {
      if (!role.person) continue;

      // Determine chamber
      let chamber = 'Government';
      if (role.role_type === 'senator') chamber = 'U.S. Senate';
      else if (role.role_type === 'representative') chamber = 'U.S. House';

      // Determine party
      let party = role.party || 'Unknown';
      if (party.toLowerCase() === 'democrat') party = 'Democratic';
      else if (party.toLowerCase() === 'republican') party = 'Republican';

      // Construct full name
      const firstName = role.person.firstname || '';
      const lastName = role.person.lastname || '';
      const fullName = role.person.name || `${firstName} ${lastName}`.trim();

      // Format district
      let district = '';
      if (role.district && parseInt(role.district) > 0) {
        district = String(role.district);
      } else if (role.role_type === 'representative') {
        district = 'At Large';
      }

      // Level: federal
      reps.push({
        name: fullName,
        party: party,
        state: stateCode,
        district: district,
        chamber: chamber,
        level: 'federal',
        phone: role.phone || '',
        website: role.website || '',
        officeName: role.title_long || role.description || '',
        officeAddress: role.extra?.office || '',
        photoUrl: '',
        imageUrl: '',
        bio: '',
        title: role.title_long || role.description || '',
        govtrackUrl: role.person.link || ''
      });
    }

    return reps;
  } catch (err) {
    console.error('GovTrack role lookup error:', err);
    return [];
  }
}

// --- OpenStates API ---
// Finds state-level legislators by state (state senators + representatives/assembly)
// Note: API caps at 10 per page, we fetch pages 1 and 2 for ~20 results
async function fetchStateLegislators(stateCode) {
  try {
    const stateLower = stateCode.toLowerCase();
    
    // Fetch first 2 pages of results (API returns max 10 per page)
    const [res1, res2] = await Promise.all([
      fetch(`https://v3.openstates.org/people?jurisdiction=${stateLower}&page=1&apikey=${OPENSTATES_API_KEY}`),
      fetch(`https://v3.openstates.org/people?jurisdiction=${stateLower}&page=2&apikey=${OPENSTATES_API_KEY}`)
    ]);

    const results = [];
    for (const res of [res1, res2]) {
      if (res.ok) {
        const data = await res.json();
        if (data.results) results.push(...data.results);
      }
    }

    const legislators = [];

    for (const person of results) {
      const role = person.current_role;
      if (!role) continue;

      // Only include state legislative roles (upper = state senate, lower = state house/assembly, legislature = DC council)
      if (role.org_classification !== 'upper' && role.org_classification !== 'lower' && role.org_classification !== 'legislature') continue;

      let chamber = 'State Legislature';
      if (role.org_classification === 'upper') chamber = 'State Senate';
      else if (role.org_classification === 'lower') chamber = 'State House';
      else if (role.org_classification === 'legislature') chamber = 'City Council';

      let party = person.party || 'Unknown';
      if (party.toLowerCase() === 'democrat') party = 'Democratic';
      else if (party.toLowerCase() === 'republican') party = 'Republican';

      legislators.push({
        name: person.name,
        party: party,
        state: stateCode,
        district: role.district || '',
        chamber: chamber,
        level: 'state',
        phone: '',
        website: '',
        officeName: role.title || '',
        officeAddress: '',
        photoUrl: person.image || '',
        imageUrl: person.image || '',
        bio: '',
        title: role.title || '',
        openstatesUrl: person.openstates_url || ''
      });
    }

    return legislators;
  } catch (err) {
    console.warn('OpenStates fetch error:', err);
    return [];
  }
}

// --- Wikipedia API ---
// Searches Wikipedia for a person and returns their page image and extract
async function enrichRepWithWikipedia(rep) {
  try {
    // Search Wikipedia for the person
    const searchUrl = `https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(rep.name + ' politician')}&format=json&origin=*&srlimit=1`;

    const searchRes = await fetch(searchUrl);
    if (!searchRes.ok) return rep;

    const searchData = await searchRes.json();
    const pages = searchData.query?.search;

    if (!pages || pages.length === 0) return rep;

    const pageTitle = pages[0].title;

    // Get page image and extract
    const pageUrl = `https://en.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(pageTitle)}&prop=pageimages|extracts&format=json&origin=*&pithumbsize=300&exintro=true&explaintext=true&exsentences=3`;

    const pageRes = await fetch(pageUrl);
    if (!pageRes.ok) return rep;

    const pageData = await pageRes.json();
    const pagesObj = pageData.query?.pages;

    if (!pagesObj) return rep;

    const pageId = Object.keys(pagesObj)[0];
    const pageInfo = pagesObj[pageId];

    if (pageInfo) {
      // Get the thumbnail image
      if (pageInfo.thumbnail?.source) {
        rep.imageUrl = pageInfo.thumbnail.source;
      }

      // Get the extract/bio
      if (pageInfo.extract) {
        rep.bio = pageInfo.extract;
      }

      // Store Wikipedia page title for linking
      rep.wikipediaTitle = pageTitle;
    }

    return rep;
  } catch (err) {
    console.warn('Wikipedia enrichment failed for', rep.name, err);
    return rep;
  }
}

// Fetch ALL representatives by ZIP code (federal + state + governor) 
// and enrich with Wikipedia data
function showCoverageNote() {
  const covNote = document.getElementById('coverage-note');
  if (!covNote || !state.reps || state.reps.length === 0) return;
  const cov = getCoverageExplanation('zip');
  covNote.style.display = 'block';
  var html = '<div style="font-size:12px;padding:10px;background:rgba(0,31,69,0.04);border-radius:6px">';
  html += '<div style="font-weight:600;color:var(--neutral);margin-bottom:4px;cursor:pointer" onclick="var e=this.nextElementSibling;e.style.display=e.style.display===\\'none\\'?\\'block\\':\\'none\\'">';
  html += 'Why some offices might be missing? <span style="font-size:10px;color:var(--neutral-lighter)">(tap to expand)</span>';
  html += '</div>';
  html += '<div style="display:none;color:var(--neutral-light);line-height:1.5">';
  html += '<p style="margin-bottom:6px">' + cov.summary + '</p>';
  html += '<p style="margin-bottom:4px;font-weight:500">Not included with just a ZIP:</p>';
  html += '<ul style="margin-left:16px;margin-bottom:6px">';
  for (var i = 0; i < cov.notFound.length; i++) {
    html += '<li style="margin-bottom:2px">' + cov.notFound[i] + '</li>';
  }
  html += '</ul>';
  html += '<p>' + cov.whatToDo + '</p>';
  html += '</div>';
  html += '</div>';
  covNote.innerHTML = html;
}
async function findReps(zip) {
  showLoading();
  try {
    // Step 1: Lookup ZIP → State
    const state = await zipToState(zip);
    if (!state) {
      hideLoading();
      showError('Could not look up that ZIP code. Please check and try again.');
      return [];
    }

    // Step 2: Fetch federal reps (Senators + Representatives) from GovTrack
    const federalReps = await fetchFederalRepsByState(state);

    // Step 3: Fetch state legislators from OpenStates
    const stateLegislators = await fetchStateLegislators(state);

    // Step 4: Add Governor from static lookup
    const governorData = STATE_GOVERNORS[state];
    const governor = governorData ? [{
      name: governorData.name,
      party: governorData.party,
      state: state,
      district: '',
      chamber: 'Governor',
      level: 'state',
      phone: '',
      website: '',
      officeName: `Governor of ${state}`,
      officeAddress: '',
      photoUrl: '',
      imageUrl: '',
      bio: '',
      title: 'Governor',
      govtrackUrl: ''
    }] : [];

    // Step 5: Combine all reps
    const allReps = [...governor, ...federalReps, ...stateLegislators];

    if (allReps.length === 0) {
      hideLoading();
      showError('No elected officials found for that ZIP code. Please check and try again.');
      return [];
    }

    // Step 6: Enrich all reps with Wikipedia data (in parallel)
    const enrichedReps = await Promise.all(
      allReps.map(rep => enrichRepWithWikipedia(rep))
    );

    hideLoading();
    return enrichedReps;
  } catch (err) {
    hideLoading();
    console.error('Failed to find reps:', err);
    showError('Could not find representatives. Please try again later.');
    return [];
  }
}

// ============================================
// RENDER FUNCTIONS
// ============================================

function renderApp() {
  const app = document.getElementById('app');
  app.innerHTML = `
    ${renderHeader()}
    <div id="screen-container"></div>
    ${renderBottomNav()}
  `;
  renderScreen(state.currentScreen);
}

function renderHeader() {
  return `
    <header class="app-header" id="app-header">
      <div class="header-left">
        <span class="header-icon-btn" id="menu-btn">
          <i data-lucide="menu" style="width:22px;height:22px"></i>
        </span>
      </div>
      <div>
        <h1>Civic Sidekick</h1>
        <div class="header-subtitle">Your Civic Dashboard</div>
      </div>
      <div class="header-right">
        <span class="header-icon-btn" id="notif-btn">
          <i data-lucide="bell" style="width:20px;height:20px"></i>
        </span>
      </div>
    </header>
  `;
}

function renderBottomNav() {
  const navItems = [
    { id: 'home', label: 'Home', icon: 'home' },
    { id: 'browse', label: 'Browse', icon: 'search' },
    { id: 'elections', label: 'Elections', icon: 'vote' },
    { id: 'events', label: 'Events', icon: 'calendar' },
    { id: 'tracking', label: 'My Bills', icon: 'file-text' },
    { id: 'settings', label: 'Settings', icon: 'settings' }
  ];
  const items = navItems.map(n => `
    <button class="nav-item${state.currentScreen === n.id ? ' active' : ''}" data-screen="${n.id}">
      <i data-lucide="${n.icon}" style="width:22px;height:22px"></i>
      <span>${n.label}</span>
    </button>
  `).join('');
  return `
    <nav class="bottom-nav" id="bottom-nav">
      ${items}
    </nav>
  `;
}

function renderScreen(screenId) {
  const container = document.getElementById('screen-container');
  const screens = {
    landing: renderLanding,
    home: renderHome,
    browse: renderBrowse,
    elections: renderElections,
    events: renderEvents,
    tracking: renderTracking,
    settings: renderSettings
  };
  const renderFn = screens[screenId] || renderHome;
  container.innerHTML = `<div class="screen active" id="screen-${screenId}"><div class="content">${renderFn()}</div></div>`;
  // Re-init Lucide icons
  if (window.lucide) lucide.createIcons();
  attachScreenListeners(screenId);
}

// --- LANDING ---
function renderLanding() {
  return `
    <div class="landing">
      <div class="landing-logo">
        <div style="position:relative;display:inline-block">
          <i data-lucide="zap" style="width:56px;height:56px;stroke-width:1.5;color:var(--primary)"></i>
          <i data-lucide="circle" style="width:64px;height:64px;stroke-width:1;color:var(--primary-light);opacity:0.2;position:absolute;top:-4px;left:-4px"></i>
        </div>
        <h1>Civic Sidekick</h1>
        <p>Your companion for civic engagement</p>
      </div>
      <div class="landing-form">
        <div class="input-group">
          <label class="input-label">Enter your ZIP Code to get started</label>
          <input type="text" class="input-field" id="landing-address" placeholder="e.g. 90210" maxlength="10" />
        </div>
        <button class="btn btn-primary btn-block btn-lg" id="landing-btn">
          <i data-lucide="arrow-right" style="width:18px;height:18px"></i>
          Get Started
        </button>
        <div id="landing-error" style="color:#c73a3a;font-size:13px;margin-top:8px;text-align:center;display:none"></div>
      </div>
      <div class="landing-footer">
        <p>Find your representatives, track legislation, and stay informed.</p>
        <a class="vote-link" href="https://vote.gov" target="_blank" rel="noopener noreferrer">
          <i data-lucide="vote" style="width:16px;height:16px"></i>
          Check or Register to Vote
        </a>
      </div>
    </div>
  `;
}

// --- HOME (Dashboard) ---
function renderHome() {
  const addressChip = state.address ? `
    <div class="address-chip">
      <i data-lucide="map-pin" style="width:14px;height:14px"></i>
      ${state.address}
    </div>
  ` : '';

  const tracked = state.trackedBills.length;
  const recentBills = state.allBills.slice(0, 3);

  let repCards = '';
  if (state.reps.length) {
    repCards = state.reps.slice(0, 2).map(r => {
      const partyColor = r.party === 'Republican' ? '#c73a3a' : '#1b6d24';
      const hasImage = r.imageUrl && r.imageUrl !== '';
      const eduInfo = window.getEducationForRep ? window.getEducationForRep(r) : null;
      const lifeImpact = eduInfo?.affectsYourLife || '';
      return `
        <div class="card" style="padding:0;">
          <div class="rep-card">
            <div class="rep-avatar" style="background:${partyColor};color:white;font-weight:700;font-size:20px;overflow:hidden">
              ${hasImage
                ? `<img src="${r.imageUrl}" alt="${r.name}" style="width:100%;height:100%;object-fit:cover" />`
                : r.name.charAt(0)
              }
            </div>
            <div class="rep-info">
              <div class="rep-name">${r.name}</div>
              <div class="rep-role">${r.chamber} - ${r.party}</div>
              <span class="rep-party">${r.state}${r.district ? '-' + r.district : ''}</span>
            </div>
            <button class="btn btn-sm btn-outline" onclick="navigate('browse')">Contact</button>
          </div>
        </div>
      `;
    }).join('');
  }

  let billsHtml = '';
  if (recentBills.length) {
    billsHtml = recentBills.map(b => {
      const statusClass = b.status.toLowerCase().replace(/\s+/g, '-');
      const isTracked = state.trackedBills.includes(b.id);
      return `
        <div class="bill-item" data-bill-id="${b.id}" style="cursor:pointer">
          <div class="bill-status status-${statusClass}"></div>
          <div class="bill-info">
            <div class="bill-id">${b.id}</div>
            <div class="bill-name">${b.title}</div>
            <div class="bill-status-text">${b.status}</div>
          </div>
          <button class="bill-track-btn${isTracked ? ' tracked' : ''}" data-bill-id="${b.id}" onclick="event.stopPropagation();toggleTrackBill('${b.id}')">
            <i data-lucide="${isTracked ? 'check' : 'plus'}" style="width:16px;height:16px"></i>
          </button>
        </div>
      `;
    }).join('');
  }

  return `
    <div class="dashboard-greeting">
      <h2>Welcome${state.address ? ', Neighbor' : ''}</h2>
      <p>Stay informed on the issues that matter to you.</p>
      ${addressChip}
    </div>

    <div class="section-header">
      <h3>Your Representatives</h3>
      <a onclick="navigate('browse')">View all</a>
    </div>
    ${repCards || `<div class="card" style="text-align:center;padding:24px;color:var(--neutral-lighter)">
      <p>Enter your address on the settings page to see your reps.</p>
    </div>`}

    <div class="section-header">
      <h3>Recent Bills</h3>
      <a onclick="navigate('browse')">Browse all</a>
    </div>
    <div class="card" style="padding:4px 16px">
      ${billsHtml || '<p style="padding:12px 0;color:var(--neutral-lighter)">No bills loaded yet.</p>'}
    </div>

    <div class="tracking-stats" style="margin-top:20px">
      <div class="stat-card">
        <div class="stat-number">${tracked}</div>
        <div class="stat-label">Tracked</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">${tracked > 0 ? Math.min(tracked, 3) : 0}</div>
        <div class="stat-label">Updates</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">${state.reps.length}</div>
        <div class="stat-label">My Reps</div>
      </div>
    </div>
  `;
}

// --- BROWSE (Bill Search & Rep Finder) ---
function renderBrowse() {
  const bills = state.allBills;
  const filtered = state.currentBillFilter
    ? bills.filter(b =>
        b.title.toLowerCase().includes(state.currentBillFilter) ||
        b.id.toLowerCase().includes(state.currentBillFilter) ||
        b.topic.toLowerCase().includes(state.currentBillFilter)
      )
    : bills;
  const totalPages = Math.ceil(filtered.length / state.itemsPerPage);
  const page = Math.min(state.currentPage, totalPages || 1);
  const start = (page - 1) * state.itemsPerPage;
  const pageBills = filtered.slice(start, start + state.itemsPerPage);

  let billsHtml = '';
  if (pageBills.length === 0) {
    billsHtml = '<p style="text-align:center;padding:24px;color:var(--neutral-lighter)">No bills found.</p>';
  } else {
    billsHtml = pageBills.map(b => {
      const statusClass = b.status.toLowerCase().replace(/\s+/g, '-');
      const isTracked = state.trackedBills.includes(b.id);
      return `
        <div class="card" style="cursor:pointer" data-bill-id="${b.id}">
          <div class="card-header">
            <div>
              <div class="card-title">${b.id}: ${b.title}</div>
              <div class="card-subtitle">Sponsor: ${b.sponsor} &middot; ${b.topic}${b.originChamber ? ' &middot; ' + b.originChamber : ''}</div>
            </div>
            <span class="card-badge badge-${statusClass === 'introduced' ? 'primary' : statusClass === 'in-committee' ? 'tertiary' : 'secondary'}">${b.status}</span>
              ${lifeImpact ? `<div style="margin-top:4px;font-size:11px;color:var(--secondary);line-height:1.4">${lifeImpact.length > 80 ? lifeImpact.substring(0, 80) + '...' : lifeImpact}</div> : ''}
          </div>
          <div class="card-body">${b.summary}</div>
          <div style="margin-top:10px;display:flex;gap:6px;flex-wrap:wrap">
            <button class="btn btn-sm ${isTracked ? 'btn-secondary' : 'btn-outline'}" data-track-bill="${b.id}" onclick="event.stopPropagation();toggleTrackBill('${b.id}')">
              <i data-lucide="${isTracked ? 'check' : 'plus'}" style="width:14px;height:14px"></i>
              ${isTracked ? 'Tracked' : 'Track'}
            </button>
            <button class="btn btn-sm btn-outline" onclick="event.stopPropagation();showBillDetail('${b.id}')">
              <i data-lucide="info" style="width:14px;height:14px"></i>
              Details
            </button>
            ${b.congressDotGovUrl ? `
              <a href="${b.congressDotGovUrl}" target="_blank" class="btn btn-sm btn-outline" onclick="event.stopPropagation()" style="font-size:11px;padding:6px 10px">
                <i data-lucide="external-link" style="width:12px;height:12px"></i>
                Congress.gov
              </a>
            ` : ''}
          </div>
        </div>
      `;
    }).join('');
  }

  // Pagination
  let pagHtml = '';
  if (totalPages > 1) {
    let pages = '';
    for (let i = 1; i <= totalPages; i++) {
      pages += `<button class="page-btn${i === page ? ' active' : ''}" data-page="${i}">${i}</button>`;
    }
    pagHtml = `<div style="display:flex;gap:6px;justify-content:center;margin:16px 0">${pages}</div>`;
  }

  // Rep finder section (with Wikipedia images)
  let repResultsHtml = '';
  if (state.reps.length) {
    repResultsHtml = state.reps.map(r => {
      const partyColor = r.party === 'Republican' ? '#c73a3a' : '#1b6d24';
      const hasImage = r.imageUrl && r.imageUrl !== '';
      const eduInfo = window.getEducationForRep ? window.getEducationForRep(r) : null;
  const roleDesc = eduInfo ? (typeof eduInfo.whatTheyDo === 'string' ? eduInfo.whatTheyDo.split('.')[0] + '.' : eduInfo.affectsYourLife.slice(0, 80) + '...') : '';
  const bioSnippet = r.bio ? r.bio.substring(0, 120) + (r.bio.length > 120 ? '...' : '') : '';
      return `
        <div class="card rep-full-card" data-rep="${r.name}" style="padding:14px;cursor:pointer">
          <div style="display:flex;align-items:center;gap:12px">
            <div style="width:48px;height:48px;border-radius:50%;background:${partyColor};color:white;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:20px;flex-shrink:0;overflow:hidden">
              ${hasImage
                ? `<img src="${r.imageUrl}" alt="${r.name}" style="width:100%;height:100%;object-fit:cover" />`
                : r.name.charAt(0)
              }
            </div>
            <div style="flex:1;min-width:0">
              <div style="font-weight:600;color:var(--primary);font-size:15px">${r.name}</div>
              <div style="font-size:12px;color:var(--neutral-lighter)">${r.chamber} - ${r.party}</div>
              <div style="font-size:11px;color:var(--neutral-light);display:flex;align-items:center;gap:4px">
                <span style="font-size:9px;font-weight:600;padding:1px 5px;border-radius:3px;background:${r.level === 'federal' ? 'rgba(0,31,69,0.08)' : 'rgba(27,109,36,0.08)'};color:${r.level === 'federal' ? 'var(--primary)' : 'var(--secondary)'}">${r.level === 'federal' ? 'FED' : 'STATE'}</span>
                ${r.state}${r.district ? ' - District ' + r.district : ''}
              </div>
            </div>
          </div>
          ${bioSnippet ? `<div style="margin-top:8px;font-size:12px;color:var(--neutral);line-height:1.5">${bioSnippet}</div>` : ''}
          <div style="margin-top:8px;display:flex;flex-wrap:wrap;gap:6px">
            ${r.phone ? `<a href="tel:${r.phone}" class="btn btn-sm btn-outline" style="font-size:11px;padding:4px 10px">📞 Call</a>` : ''}
            ${r.website ? `<a href="${r.website}" target="_blank" class="btn btn-sm btn-outline" style="font-size:11px;padding:4px 10px">🌐 Website</a>` : ''}
            ${r.wikipediaTitle ? `<a href="https://en.wikipedia.org/wiki/${encodeURIComponent(r.wikipediaTitle)}" target="_blank" class="btn btn-sm btn-outline" style="font-size:11px;padding:4px 10px">📖 Wikipedia</a>` : ''}
            ${r.govtrackUrl ? `<a href="${r.govtrackUrl}" target="_blank" class="btn btn-sm btn-outline" style="font-size:11px;padding:4px 10px">🏛️ GovTrack</a>` : ''}
            ${r.openstatesUrl ? `<a href="${r.openstatesUrl}" target="_blank" class="btn btn-sm btn-outline" style="font-size:11px;padding:4px 10px">📋 OpenStates</a>` : ''}
          </div>
        </div>
      `;
    }).join('');
  }

  return `
    <!-- Bill Search -->
    <div style="margin-bottom:20px">
      <div class="search-bar">
        <i data-lucide="search" style="width:18px;height:18px"></i>
        <input type="text" id="bill-search-input" placeholder="Search bills by title, ID, or topic..." value="${state.currentBillFilter}" />
      </div>
    </div>

    <!-- Bill List -->
    <div style="margin-bottom:8px;display:flex;justify-content:space-between;align-items:center">
      <h3 style="font-size:16px;font-weight:600;color:var(--neutral)">Bills</h3>
      <span style="font-size:12px;color:var(--neutral-lighter)">${filtered.length} found</span>
    </div>
    ${billsHtml}
    ${pagHtml}

    <!-- Find Your Representative -->
    <div style="margin-top:32px;padding-top:20px;border-top:1px solid var(--neutral-lightest)">
      <h3 style="font-size:16px;font-weight:600;color:var(--neutral);margin-bottom:12px">Find Your Representative</h3>
      <div style="display:flex;gap:8px">
        <input type="text" class="input-field" id="rep-search-input" placeholder="Enter your address or ZIP..." style="flex:1" />
        <button class="btn btn-primary" id="rep-search-btn">Search</button>
      </div>
      <div id="rep-results" style="margin-top:12px">
        ${repResultsHtml}
      </div>
    </div>

    <!-- Bill Detail Modal -->
    <div id="bill-detail-modal" style="display:none"></div>
  `;
}

// --- EVENTS ---
function renderEvents() {
  const events = [
    { month: 'MAR', day: '15', title: 'House Budget Committee Hearing', desc: 'FY2026 Budget Resolution markup', time: '10:00 AM EST' },
    { month: 'MAR', day: '22', title: 'Senate Judiciary Committee', desc: 'Nomination hearing for Circuit Court', time: '2:00 PM EST' },
    { month: 'APR', day: '5', title: 'Education & Labor Subcommittee', desc: 'Hearing on higher education reform', time: '9:30 AM EST' },
    { month: 'APR', day: '12', title: 'House Floor Vote', desc: 'HR-142: Climate Resilience Act', time: 'TBD' }
  ];

  const eventsHtml = events.map(e => `
    <div class="card" style="padding:0;">
      <div class="event-card">
        <div class="event-date">
          <span class="month">${e.month}</span>
          <span class="day">${e.day}</span>
        </div>
        <div class="event-info">
          <div class="event-title">${e.title}</div>
          <div class="event-desc">${e.desc}</div>
          <div class="event-time">
            <i data-lucide="clock" style="width:14px;height:14px"></i>
            ${e.time}
          </div>
          <button class="btn btn-sm btn-outline event-cal-btn">
            <i data-lucide="calendar-plus" style="width:14px;height:14px"></i>
            Add to Calendar
          </button>
        </div>
      </div>
    </div>
  `).join('');

  return `
    <div class="cal-sync-card">
      <h3>📅 Stay Informed</h3>
      <p>Upcoming committee hearings, floor votes, and civic events.</p>
      <div class="cal-sync-status">
        <span class="dot"></span>
        Calendar sync ready
      </div>
    </div>
    ${eventsHtml}
    <div style="text-align:center;margin-top:12px">
      <button class="btn btn-outline btn-block">
        <i data-lucide="external-link" style="width:16px;height:16px"></i>
        Sync with Google Calendar
      </button>
    </div>
  `;
}

// --- TRACKING (My Bills) ---
function renderTracking() {
  const tracked = state.allBills.filter(b => state.trackedBills.includes(b.id));
  const trackedCount = tracked.length;

  let billsHtml = '';
  if (tracked.length === 0) {
    billsHtml = `
      <div class="empty-state">
        <i data-lucide="file-text" style="width:48px;height:48px;stroke-width:1.5"></i>
        <h3>No bills tracked yet</h3>
        <p>Browse legislation and tap the track button to start following bills.</p>
        <button class="btn btn-primary mt-16" onclick="navigate('browse')">
          <i data-lucide="search" style="width:16px;height:16px"></i>
          Browse Bills
        </button>
      </div>
    `;
  } else {
    billsHtml = tracked.map(b => {
      const statusClass = b.status.toLowerCase().replace(/\s+/g, '-');
      return `
        <div class="card" style="cursor:pointer" data-bill-id="${b.id}">
          <div class="card-header">
            <div>
              <div class="card-title">${b.id}: ${b.title}</div>
              <div class="card-subtitle">${b.topic} &middot; ${b.sponsor}</div>
            </div>
            <span class="card-badge badge-${statusClass === 'introduced' ? 'primary' : statusClass === 'in-committee' ? 'tertiary' : 'secondary'}">${b.status}</span>
          </div>
          <div style="margin-top:8px;display:flex;gap:8px;align-items:center">
            <button class="btn btn-sm btn-outline" onclick="event.stopPropagation();showBillDetail('${b.id}')">
              <i data-lucide="info" style="width:14px;height:14px"></i>
              Details
            </button>
            <button class="btn btn-sm" style="background:var(--error);color:white;border:none" onclick="event.stopPropagation();toggleTrackBill('${b.id}')">
              <i data-lucide="x" style="width:14px;height:14px"></i>
              Remove
            </button>
          </div>
        </div>
      `;
    }).join('');
  }

  return `
    <div class="tracking-stats" style="margin-bottom:20px">
      <div class="stat-card">
        <div class="stat-number">${trackedCount}</div>
        <div class="stat-label">Tracked</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">${trackedCount > 0 ? Math.min(trackedCount, 3) : 0}</div>
        <div class="stat-label">Updates</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">0</div>
        <div class="stat-label">Actions</div>
      </div>
    </div>
    ${billsHtml}
  `;
}

// --- SETTINGS ---
function renderElections() {
  // Render the comprehensive election education hub
  const edu = window.ELECTION_EDUCATION || ELECTION_EDUCATION;

  function renderLevelSection(levelData) {
    const positions = levelData.positions.map(p => {
      const searchStatus = p.searchable === true ? 'check-circle' : p.searchable === false ? 'x-circle' : 'info';
      const searchColor = p.searchable === true ? 'var(--secondary)' : p.searchable === false ? 'var(--neutral-lighter)' : 'var(--tertiary)';
      const searchLabel = p.searchable === true ? 'Always in results' : p.searchable === false ? 'Not available' : 'Partial coverage';

      let whatTheyDoHtml = '';
      if (typeof p.whatTheyDo === 'string') {
        whatTheyDoHtml = `<p style="font-size:13px;color:var(--neutral);line-height:1.6;margin-bottom:8px">${p.whatTheyDo}</p>`;
      } else if (typeof p.whatTheyDo === 'object') {
        whatTheyDoHtml = Object.entries(p.whatTheyDo).map(([role, desc]) => `
          <div style="margin-bottom:6px;padding-left:12px;border-left:2px solid var(--neutral-lightest)">
            <strong style="font-size:12px;color:var(--primary)">${role}:</strong>
            <span style="font-size:12px;color:var(--neutral)"> ${desc}</span>
          </div>
        `).join('');
      }

      return `
        <div class="card" style="margin-bottom:12px;overflow:hidden">
          <div class="edu-role-header" style="cursor:pointer;padding:14px;display:flex;align-items:center;gap:10px;background:var(--neutral-lightest);border-bottom:1px solid var(--neutral-lightest)" onclick="this.nextElementSibling.style.display = this.nextElementSibling.style.display === 'none' ? 'block' : 'none'">
            <i data-lucide="${searchStatus}" style="width:16px;height:16px;color:${searchColor};flex-shrink:0"></i>
            <div style="flex:1">
              <div style="font-weight:600;font-size:14px;color:var(--primary)">${p.title}</div>
              <div style="font-size:11px;color:${searchColor};font-weight:500">${searchLabel}</div>
            </div>
            <i data-lucide="chevron-down" style="width:16px;height:16px;color:var(--neutral-lighter)"></i>
          </div>
          <div class="edu-role-body" style="padding:14px;display:none">
            ${p.howElected ? `<div style="margin-bottom:10px"><strong style="font-size:12px;color:var(--neutral)">How elected:</strong><p style="font-size:12px;color:var(--neutral-lighter);margin-top:2px">${p.howElected}</p></div>` : ''}
            <div style="margin-bottom:10px">
              <strong style="font-size:12px;color:var(--neutral)">What they do:</strong>
              ${whatTheyDoHtml}
            </div>
            <div style="margin-bottom:10px;padding:10px;background:rgba(27,109,36,0.06);border-radius:6px;border-left:3px solid var(--secondary)">
              <strong style="font-size:12px;color:var(--secondary)">How this affects you:</strong>
              <p style="font-size:12px;color:var(--neutral);margin-top:2px;line-height:1.5">${p.affectsYourLife}</p>
            </div>
            ${p.districtNote ? `<div style="font-size:11px;color:var(--neutral-light);margin-bottom:6px">${p.districtNote}</div>` : ''}
            ${p.nextElection ? `<div style="font-size:11px;color:var(--tertiary);margin-bottom:6px">Next election: ${p.nextElection}</div>` : ''}
            ${p.limitationNote ? `<div style="font-size:11px;color:var(--neutral-lighter);margin-top:6px;padding-top:6px;border-top:1px solid var(--neutral-lightest)">${p.limitationNote}</div>` : ''}
            ${p.source ? `<div style="font-size:10px;color:var(--neutral-lighter);margin-top:4px">Data source: ${p.source}</div>` : ''}
          </div>
        </div>
      `;
    }).join('');

    return `
      <div style="margin-bottom:24px">
        <h3 style="font-size:17px;font-weight:700;color:var(--primary);margin-bottom:4px">${levelData.title}</h3>
        <p style="font-size:13px;color:var(--neutral-lighter);margin-bottom:12px">${levelData.subtitle}</p>
        ${levelData.total ? `<p style="font-size:12px;color:var(--neutral-light);margin-bottom:10px">Total: ${levelData.total}</p>` : ''}
        ${positions}
        ${levelData.summaryNote ? `<div style="font-size:12px;color:var(--neutral-light);padding:10px;background:rgba(0,31,69,0.04);border-radius:6px;margin-top:4px">${levelData.summaryNote}</div>` : ''}
      </div>
    `;
  }

  function renderCoverageSection() {
    const cov = edu.coverage;
    return `
      <div style="margin-top:8px;margin-bottom:24px">
        <h3 style="font-size:17px;font-weight:700;color:var(--primary);margin-bottom:12px">What We Can & Cannot Find</h3>

        <div style="margin-bottom:12px">
          <h4 style="font-size:13px;font-weight:600;color:var(--secondary);margin-bottom:6px">Reliably found:</h4>
          ${cov.reliable.map(r => `<div style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--neutral);padding:3px 0"><i data-lucide="check-circle" style="width:14px;height:14px;color:var(--secondary);flex-shrink:0"></i>${r}</div>`).join('')}
        </div>

        <div style="margin-bottom:12px">
          <h4 style="font-size:13px;font-weight:600;color:var(--tertiary);margin-bottom:6px">Partially found (try full address):</h4>
          ${cov.partial.map(r => `<div style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--neutral);padding:3px 0"><i data-lucide="info" style="width:14px;height:14px;color:var(--tertiary);flex-shrink:0"></i>${r}</div>`).join('')}
        </div>

        <div style="margin-bottom:12px">
          <h4 style="font-size:13px;font-weight:600;color:var(--neutral-lighter);margin-bottom:6px">Not available via our APIs:</h4>
          ${cov.notAvailable.map(r => `<div style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--neutral-lighter);padding:3px 0"><i data-lucide="x-circle" style="width:14px;height:14px;color:var(--neutral-lighter);flex-shrink:0"></i>${r}</div>`).join('')}
        </div>

        <div style="padding:10px;background:rgba(0,31,69,0.04);border-radius:6px;font-size:12px;color:var(--neutral-light);line-height:1.5">
          ${cov.fallbackMessage}
        </div>
      </div>
    `;
  }

  return `
    <div style="margin-bottom:20px">
      <h2 style="font-size:24px;font-weight:700;color:var(--primary);margin-bottom:4px">Know Your Elections</h2>
      <p style="font-size:14px;color:var(--neutral-lighter);margin-bottom:16px">Learn about every office you vote for � what they do and how they affect your daily life.</p>
    </div>

    ${renderLevelSection(edu.federal)}
    ${renderLevelSection(edu.state)}
    ${renderLevelSection(edu.local)}
    ${renderCoverageSection()}

    <div style="text-align:center;margin:24px 0;padding:16px;background:linear-gradient(135deg,rgba(0,31,69,0.03),rgba(27,109,36,0.03));border-radius:12px">
      <p style="font-size:13px;color:var(--neutral);margin-bottom:8px">Ready to find your specific representatives?</p>
      <button class="btn btn-primary" onclick="navigate('settings')">
        <i data-lucide="map-pin" style="width:16px;height:16px"></i>
        Enter Your Address
      </button>
    </div>
  `;
}

function renderSettings() {
  const topicPrefs = ['Education', 'Healthcare', 'Climate', 'Tax Reform', 'Immigration', 'Veterans'];
  const topicHtml = topicPrefs.map(t => `
    <div class="settings-item">
      <div class="settings-item-left">
        <div class="settings-item-label">${t}</div>
      </div>
      <button class="toggle active" data-topic="${t.toLowerCase()}"></button>
    </div>
  `).join('');

  return `
    <div class="settings-section">
      <h4>Location</h4>
      <div class="card" style="padding:16px">
        <div style="display:flex;gap:8px">
          <input type="text" class="input-field" id="settings-address" placeholder="Enter your address" value="${state.address}" style="flex:1" />
          <button class="btn btn-primary" id="settings-save-address">Save</button>
        </div>
        ${state.address ? `
          <div class="address-chip" style="margin-top:8px">
            <i data-lucide="map-pin" style="width:14px;height:14px"></i>
            ${state.address}
          </div>
        ` : ''}
      </div>
    </div>

    <div class="settings-section">
      <h4>Notifications</h4>
      <div class="card" style="padding:4px 16px">
        <div class="settings-item">
          <div class="settings-item-left">
            <i data-lucide="bell" style="width:20px;height:20px"></i>
            <div>
              <div class="settings-item-label">Push Notifications</div>
              <div class="settings-item-desc">Get alerts on bill updates</div>
            </div>
          </div>
          <button class="toggle active" id="toggle-push"></button>
        </div>
        <div class="settings-item">
          <div class="settings-item-left">
            <i data-lucide="mail" style="width:20px;height:20px"></i>
            <div>
              <div class="settings-item-label">Email Digest</div>
              <div class="settings-item-desc">Weekly summary of tracked bills</div>
            </div>
          </div>
          <button class="toggle" id="toggle-email"></button>
        </div>
      </div>
    </div>

    <div class="settings-section">
      <h4>Topics of Interest</h4>
      <div class="card" style="padding:4px 16px">
        ${topicHtml}
      </div>
    </div>

    <div class="privacy-notice">
      <i data-lucide="shield" style="width:18px;height:18px;color:var(--primary);flex-shrink:0;margin-top:2px"></i>
      <p><strong>Your privacy matters.</strong> Your ZIP code is only used to find your representatives via public APIs. No data is stored on our servers.</p>
    </div>

    <div style="text-align:center;margin-top:16px">
      <p style="font-size:12px;color:var(--neutral-lighter)">Civic Sidekick v1.0 &middot; Data from GovTrack.us, OpenStates &amp; Wikipedia</p>
    </div>
  `;
}

// ============================================
// EVENT LISTENERS / ACTIONS
// ============================================

function attachScreenListeners(screenId) {
  // Nav items
  document.querySelectorAll('.nav-item').forEach(el => {
    el.addEventListener('click', () => {
      const screen = el.dataset.screen;
      if (screen) navigate(screen);
    });
  });

  // Landing
  if (screenId === 'landing') {
    const btn = document.getElementById('landing-btn');
    const input = document.getElementById('landing-address');
    const errEl = document.getElementById('landing-error');
    if (btn && input) {
      const handler = async () => {
        const val = input.value.trim();
        // Accept ZIP code (5 digits) or ZIP+4
        const isValidZip = /^\d{5}(-\d{4})?$/.test(val);
        if (!val) {
          errEl.textContent = 'Please enter your ZIP Code.';
          errEl.style.display = 'block';
          return;
        }
        if (!isValidZip) {
          errEl.textContent = 'Please enter a valid 5-digit ZIP Code.';
          errEl.style.display = 'block';
          return;
        }
        errEl.style.display = 'none';
        btn.disabled = true;
        btn.innerHTML = '<i data-lucide="loader" style="width:18px;height:18px" class="spin"></i> Loading...';
        if (window.lucide) lucide.createIcons();

        state.address = val;
        state.reps = await findReps(val);

        btn.disabled = false;
        btn.innerHTML = '<i data-lucide="arrow-right" style="width:18px;height:18px"></i> Get Started';
        if (window.lucide) lucide.createIcons();

        if (state.reps.length > 0) {
          navigate('home');
        }
      };
      btn.addEventListener('click', handler);
      input.addEventListener('keypress', e => { if (e.key === 'Enter') handler(); });
    }
  }

  // Browse
  if (screenId === 'browse') {
    // Search input
    const searchInput = document.getElementById('bill-search-input');
    if (searchInput) {
      let debounceTimer;
      searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
          state.currentBillFilter = searchInput.value.trim().toLowerCase();
          state.currentPage = 1;
          renderScreen('browse');
        }, 300);
      });
    }

    // Pagination
    document.querySelectorAll('.page-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        state.currentPage = parseInt(btn.dataset.page, 10);
        renderScreen('browse');
      });
    });

    // Rep search (Google Civic + Wikipedia)
    const repBtn = document.getElementById('rep-search-btn');
    const repInput = document.getElementById('rep-search-input');
    if (repBtn && repInput) {
      const repHandler = async () => {
        const val = repInput.value.trim();
        if (val) {
          repBtn.disabled = true;
          repBtn.textContent = 'Searching...';
          hideError();

          state.address = val;
          state.reps = await findReps(val);

          repBtn.disabled = false;
          repBtn.textContent = 'Search';

          if (state.reps.length > 0) {
            renderScreen('browse');
          }
        }
      };
      repBtn.addEventListener('click', repHandler);
      repInput.addEventListener('keypress', e => { if (e.key === 'Enter') repHandler(); });
    }

    // Rep card click - show detail modal with bio
    document.querySelectorAll('.rep-full-card').forEach(card => {
      card.addEventListener('click', () => {
        const name = card.dataset.rep;
        const rep = state.reps.find(r => r.name === name);
        if (rep) showRepDetail(rep);
      });
    });
  }

  // Settings
  if (screenId === 'settings') {
    const saveBtn = document.getElementById('settings-save-address');
    const addrInput = document.getElementById('settings-address');
    if (saveBtn && addrInput) {
      saveBtn.addEventListener('click', async () => {
        const val = addrInput.value.trim();
        if (val) {
          saveBtn.disabled = true;
          saveBtn.textContent = 'Saving...';
          hideError();

          state.address = val;
          state.reps = await findReps(val);

          saveBtn.disabled = false;
          saveBtn.textContent = 'Save';

          if (state.reps.length > 0) {
            renderScreen('settings');
            showToast('Address saved with ' + state.reps.length + ' representatives found');
          }
        }
      });
    }

    // Toggles
    document.querySelectorAll('.toggle').forEach(toggle => {
      toggle.addEventListener('click', () => {
        toggle.classList.toggle('active');
      });
    });
  }
}

// --- Global Functions (used in inline onclick) ---
window.navigate = function(screenId) {
  state.currentScreen = screenId;
  const header = document.getElementById('app-header');
  if (header) {
    header.style.display = screenId === 'landing' ? 'none' : 'flex';
  }
  const nav = document.getElementById('bottom-nav');
  if (nav) {
    nav.style.display = screenId === 'landing' ? 'none' : 'flex';
  }
  renderScreen(screenId);
  // Update nav active states
  document.querySelectorAll('.nav-item').forEach(el => {
    el.classList.toggle('active', el.dataset.screen === screenId);
  });
};

window.toggleTrackBill = function(billId) {
  const idx = state.trackedBills.indexOf(billId);
  if (idx > -1) {
    state.trackedBills.splice(idx, 1);
    showToast('Removed from tracking');
  } else {
    state.trackedBills.push(billId);
    showToast('Bill added to tracking');
  }
  // Re-render current screen
  renderScreen(state.currentScreen);
};

window.showBillDetail = function(billId) {
  const bill = state.allBills.find(b => b.id === billId);
  if (!bill) return;
  const statusClass = bill.status.toLowerCase().replace(/\s+/g, '-');
  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay open';
  overlay.innerHTML = `
    <div class="modal-content">
      <div class="modal-handle"></div>
      <div class="modal-title">${bill.id}: ${bill.title}</div>
      <div style="margin-bottom:12px">
        <span class="card-badge badge-${statusClass === 'introduced' ? 'primary' : statusClass === 'in-committee' ? 'tertiary' : 'secondary'}">${bill.status}</span>
      </div>
      <div class="detail-section" style="padding:0">
        <p style="margin-bottom:12px">${bill.summary}</p>
        <p style="font-size:13px;color:var(--neutral-light)"><strong>Sponsor:</strong> ${bill.sponsor}</p>
        <p style="font-size:13px;color:var(--neutral-light)"><strong>Topic:</strong> ${bill.topic}</p>
        <p style="font-size:13px;color:var(--neutral-light)"><strong>Introduced:</strong> ${bill.introduced || 'N/A'}</p>
        ${bill.originChamber ? `<p style="font-size:13px;color:var(--neutral-light)"><strong>Origin:</strong> ${bill.originChamber}</p>` : ''}
      </div>
      <div style="display:flex;flex-direction:column;gap:8px;margin-top:16px">
        ${bill.congressDotGovUrl ? `
          <a href="${bill.congressDotGovUrl}" target="_blank" class="btn btn-outline btn-block btn-sm" style="justify-content:flex-start">
            <i data-lucide="external-link" style="width:16px;height:16px"></i>
            View Full Bill
          </a>
        ` : ''}
      </div>
      <button class="btn btn-primary btn-block mt-16" onclick="this.closest('.modal-overlay').remove()">Close</button>
    </div>
  `;
  overlay.addEventListener('click', e => { if (e.target === overlay) overlay.remove(); });
  document.body.appendChild(overlay);
  if (window.lucide) lucide.createIcons();
};

// --- Representative Detail Modal (with Wikipedia bio) ---
window.showRepDetail = function(rep) {
  const partyColor = rep.party === 'Republican' ? '#c73a3a' : '#1b6d24';
  const hasImage = rep.imageUrl && rep.imageUrl !== '';

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay open';
  overlay.innerHTML = `
    <div class="modal-content">
      <div class="modal-handle"></div>
      <div style="text-align:center;margin-bottom:16px">
        <div style="width:80px;height:80px;border-radius:50%;background:${partyColor};color:white;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:32px;margin:0 auto 12px;overflow:hidden">
          ${hasImage
            ? `<img src="${rep.imageUrl}" alt="${rep.name}" style="width:100%;height:100%;object-fit:cover" />`
            : rep.name.charAt(0)
          }
        </div>
        <div class="modal-title" style="margin-bottom:4px">${rep.name}</div>
        <div style="font-size:14px;color:var(--neutral-lighter)">
          ${rep.chamber} - ${rep.party}
          ${rep.state ? '&middot; ' + rep.state + (rep.district ? ' - District ' + rep.district : '') : ''}
        </div>
      </div>

      ${rep.bio ? `
        <div style="margin-bottom:16px;padding:12px;background:var(--bg);border-radius:var(--radius-sm)">
          <h4 style="font-size:12px;font-weight:700;color:var(--neutral-lighter);text-transform:uppercase;margin-bottom:6px">Biography</h4>
          <p style="font-size:13px;color:var(--neutral);line-height:1.7">${rep.bio}</p>
        </div>
      ` : ''}

      ${rep.officeAddress ? `
        <div style="margin-bottom:12px;padding:10px 12px;background:var(--bg);border-radius:var(--radius-sm);font-size:12px;color:var(--neutral-light)">
          <strong>Washington Office:</strong> ${rep.officeAddress}, Washington DC
        </div>
      ` : ''}

      <div style="display:flex;flex-direction:column;gap:8px">
        ${rep.phone ? `
          <a href="tel:${rep.phone}" class="btn btn-outline btn-block btn-sm" style="justify-content:flex-start">
            📞 ${rep.phone}
          </a>
        ` : ''}
        ${rep.website ? `
          <a href="${rep.website}" target="_blank" class="btn btn-outline btn-block btn-sm" style="justify-content:flex-start">
            🌐 ${rep.website}
          </a>
        ` : ''}
        ${rep.wikipediaTitle ? `
          <a href="https://en.wikipedia.org/wiki/${encodeURIComponent(rep.wikipediaTitle)}" target="_blank" class="btn btn-outline btn-block btn-sm" style="justify-content:flex-start">
            📖 Wikipedia: ${rep.wikipediaTitle}
          </a>
        ` : ''}
        ${rep.govtrackUrl ? `
          <a href="${rep.govtrackUrl}" target="_blank" class="btn btn-outline btn-block btn-sm" style="justify-content:flex-start">
            🏛️ GovTrack Profile
          </a>
        ` : ''}
        ${rep.openstatesUrl ? `
          <a href="${rep.openstatesUrl}" target="_blank" class="btn btn-outline btn-block btn-sm" style="justify-content:flex-start">
            📋 OpenStates Profile
          </a>
        ` : ''}
      </div>

      <button class="btn btn-primary btn-block mt-16" onclick="this.closest('.modal-overlay').remove()">Close</button>
    </div>
  `;
  overlay.addEventListener('click', e => { if (e.target === overlay) overlay.remove(); });
  document.body.appendChild(overlay);
  if (window.lucide) lucide.createIcons();
};

// --- Toast Notifications ---
function showToast(message) {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s';
    setTimeout(() => toast.remove(), 300);
  }, 2000);
}

// ============================================
// INIT
// ============================================

document.addEventListener('DOMContentLoaded', async () => {
  // Load bills
  await fetchBills();
  // Render app
  renderApp();
  // Hide header & nav on landing
  const header = document.getElementById('app-header');
  if (header) header.style.display = 'none';
  const nav = document.getElementById('bottom-nav');
  if (nav) nav.style.display = 'none';
  // Init Lucide
  if (window.lucide) lucide.createIcons();
});
