// ============================================
// Civic Sidekick - Main Application JavaScript
// Version: 2.0 (MVP) - Rep Finder + Education
// ============================================

// --- State ---
const state = {
  currentScreen: 'landing',
  address: '',
  reps: [],
  zipCode: ''
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
// API CONFIG
// ============================================
const PROPUBLICA_API_KEY = 'YOUR_PROPUBLICA_API_KEY'; // Get free at propublica.org/datastore/
const OPENSTATES_API_KEY = '14758350-d06d-4c26-9ec0-cd8060abebd7';

// ============================================
// DATA / API FUNCTIONS
// ============================================

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


// --- ProPublica Congress API ---
async function fetchFederalRepsByState(stateAbbr) {
  showLoading();
  try {
    const [senateRes, houseRes] = await Promise.all([
      fetch(`https://api.propublica.org/congress/v1/119/senate/members.json`, {
        headers: { 'X-API-Key': PROPUBLICA_API_KEY }
      }),
      fetch(`https://api.propublica.org/congress/v1/119/house/members.json`, {
        headers: { 'X-API-Key': PROPUBLICA_API_KEY }
      })
    ]);
    if (!senateRes.ok || !houseRes.ok) throw new Error('ProPublica API error');
    const senateData = await senateRes.json();
    const houseData = await houseRes.json();
    const allMembers = [
      ...(senateData.results?.[0]?.members || []),
      ...(houseData.results?.[0]?.members || [])
    ];
    const reps = allMembers
      .filter(m => m.state === stateAbbr)
      .map(m => ({
        name: m.first_name + ' ' + m.last_name,
        party: m.party === 'D' ? 'Democratic' : m.party === 'R' ? 'Republican' : m.party || '',
        state: m.state,
        district: m.district || '',
        chamber: m.chamber === 'Senate' ? 'U.S. Senate' : 'U.S. House',
        level: 'federal',
        phone: m.phone || '',
        website: m.url || '',
        email: m.email || '',
        officeName: '',
        officeAddress: m.office || '',
        photoUrl: '',
        imageUrl: '',
        bio: '',
        title: m.short_title || (m.chamber === 'Senate' ? 'Senator' : 'Representative'),
        govtrackUrl: '',
        openstatesUrl: '',
        wikipediaTitle: m.wikipedia_id || m.first_name + '_' + m.last_name,
        localRole: ''
      }));
    hideLoading();
    return reps;
  } catch (err) {
    console.error('ProPublica API error:', err);
    hideLoading();
    showError('Could not fetch federal reps via ProPublica. Trying fallback...');
    return fetchFederalRepsFallback(stateAbbr);
  }
}

// --- GovTrack.us Fallback (no API key) ---
async function fetchFederalRepsFallback(stateCode) {
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



// --- Main Rep Finder ---
async function findReps(zip) {
  if (!zip || zip.length < 5) return;
  showLoading();
  state.zipCode = zip;
  state.address = zip;
  try {
    const stateAbbr = await zipToState(zip);
    if (!stateAbbr) {
      hideLoading();
      showError('Could not determine state from that ZIP code.');
      return;
    }
    let allReps = await fetchFederalRepsByState(stateAbbr);
    const gov = STATE_GOVERNORS[stateAbbr];
    if (gov) {
      allReps.push({
        name: gov.name,
        party: gov.party,
        state: stateAbbr,
        district: '',
        chamber: 'Governor',
        level: 'state',
        phone: '',
        website: '',
        email: '',
        officeName: 'Governor',
        officeAddress: '',
        photoUrl: '',
        imageUrl: '',
        bio: '',
        title: 'Governor',
        govtrackUrl: '',
        openstatesUrl: '',
        wikipediaTitle: gov.name.replace(/ /g, '_'),
        localRole: ''
      });
    }
    const stateLegislators = await fetchStateLegislators(stateAbbr);
    allReps = allReps.concat(stateLegislators);
    const enrichedReps = [];
    for (const rep of allReps) {
      enrichedReps.push(await enrichRepWithWikipedia(rep));
    }
    state.reps = enrichedReps;
    renderScreen('home');
    if (window.lucide) lucide.createIcons();
    showCoverageNote();
    showToast('Found ' + enrichedReps.length + ' officials');
  } catch (err) {
    console.error('findReps error:', err);
    showError('Something went wrong. Please try again.');
  } finally {
    hideLoading();
  }
}


// ============================================
// RENDER FUNCTIONS
// ============================================

function renderApp() {
  const app = document.getElementById('app');
  app.innerHTML = renderHeader() + renderBottomNav() + '<main id="main-content"></main>';
  renderScreen(state.currentScreen);
  if (window.lucide) lucide.createIcons();
}

function renderHeader() {
  return '<header id="app-header">' +
    '<div class="header-inner">' +
      '<div class="logo" onclick="navigate(\'home\')">' +
        '<span class="logo-icon">&#9889;</span>' +
        '<span class="logo-text">Civic Sidekick</span>' +
      '</div>' +
    '</div>' +
  '</header>';
}

function renderBottomNav() {
  const navItems = [
    { id: 'home', label: 'Home', icon: 'home' },
    { id: 'elections', label: 'Elections', icon: 'vote' },
    { id: 'events', label: 'Events', icon: 'calendar' },
    { id: 'settings', label: 'Settings', icon: 'settings' }
  ];
  const items = navItems.map(function(n) {
    var active = state.currentScreen === n.id ? ' active' : '';
    return '<button class="nav-item' + active + '" data-screen="' + n.id + '">' +
      '<i data-lucide="' + n.icon + '" style="width:22px;height:22px"></i>' +
      '<span>' + n.label + '</span>' +
    '</button>';
  }).join('');
  return '<nav id="bottom-nav">' + items + '</nav>';
}

function navigate(screen) {
  state.currentScreen = screen;
  renderScreen(screen);
  if (window.lucide) lucide.createIcons();
}

const screens = {
  landing: renderLanding,
  home: renderHome,
  elections: renderElections,
  events: renderEvents,
  settings: renderSettings
};

function renderScreen(screenId) {
  var main = document.getElementById('main-content');
  if (!main) return;
  state.currentScreen = screenId;
  document.querySelectorAll('.nav-item').forEach(function(n) {
    n.classList.toggle('active', n.dataset.screen === screenId);
  });
  if (screens[screenId]) {
    main.innerHTML = screens[screenId]();
  } else {
    main.innerHTML = screens.home();
  }
  attachScreenListeners(screenId);
}

function renderLanding() {
  return '<div class="landing">' +
    '<div class="landing-content">' +
      '<div class="landing-icon">&#9889;</div>' +
      '<h1>Civic Sidekick</h1>' +
      '<p class="landing-subtitle">Your companion for civic engagement.</p>' +
      '<p class="landing-desc">Enter your ZIP code and instantly see every person representing you.</p>' +
      '<div class="zip-input-group">' +
        '<input type="text" class="input-field" id="landing-zip" placeholder="Enter your ZIP code" maxlength="10" autofocus />' +
        '<button class="btn btn-primary" id="landing-go">Find My Reps</button>' +
      '</div>' +
      '<div class="landing-privacy">' +
        '<i data-lucide="shield" style="width:14px;height:14px"></i> No signup. No tracking. Your ZIP goes straight to public APIs.' +
      '</div>' +
    '</div>' +
  '</div>';
}

function renderHome() {
  var addressChip = state.address ?
    '<div class="address-chip"><i data-lucide="map-pin" style="width:14px;height:14px"></i> ' + state.address + '</div>' : '';
  var repCards = '';
  if (state.reps.length) {
    repCards = state.reps.map(function(r) {
      var partyColor = r.party === 'Republican' ? '#c73a3a' : '#1b6d24';
      var eduInfo = window.getEducationForRep ? window.getEducationForRep(r) : null;
      var lifeImpact = eduInfo ? eduInfo.affectsYourLife || '' : '';
      var initial = r.name ? r.name.charAt(0).toUpperCase() : '?';
      var hasImage = r.imageUrl && r.imageUrl !== '';
      var levelBadge = r.level === 'federal' ? 'FEDERAL' : r.level === 'state' ? 'STATE' : 'LOCAL';
      var levelColor = r.level === 'federal' ? 'var(--primary)' : r.level === 'state' ? 'var(--tertiary)' : 'var(--secondary)';
      var lifeHtml = lifeImpact ? '<div class="life-impact-badge" style="margin-top:6px">' + (lifeImpact.length > 100 ? lifeImpact.substring(0, 100) + '...' : lifeImpact) + '</div>' : '';
      var bioHtml = r.bio ? '<div style="margin-top:8px;font-size:12px;color:var(--neutral);line-height:1.5">' + r.bio.substring(0, 120) + (r.bio.length > 120 ? '...' : '') + '</div>' : '';
      var roleDesc = eduInfo ? (typeof eduInfo.whatTheyDo === 'string' ? eduInfo.whatTheyDo.split('.')[0] + '.' : eduInfo.affectsYourLife.slice(0, 80) + '...') : '';
      var roleHtml = roleDesc ? '<div class="life-impact-badge" style="margin-top:6px;margin-bottom:4px">' + roleDesc + '</div>' : '';
      var imgHtml = hasImage ? '<img src="' + r.imageUrl + '" style="width:100%;height:100%;object-fit:cover" alt="" />' : initial;
      var phoneHtml = r.phone ? '<a href="tel:' + r.phone + '" class="btn btn-sm btn-outline" style="text-decoration:none">&#9742; Call</a>' : '';
      var webHtml = r.website ? '<a href="' + r.website + '" target="_blank" class="btn btn-sm btn-outline">&#127760; Website</a>' : '';
      return '<div class="card rep-card" style="padding:14px;cursor:pointer">' +
        '<div style="display:flex;gap:14px;align-items:flex-start">' +
          '<div class="rep-avatar" style="background:' + partyColor + ';width:48px;height:48px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:20px;font-weight:700;color:white;flex-shrink:0;overflow:hidden">' +
            imgHtml + '</div>' +
          '<div style="flex:1;min-width:0">' +
            '<div style="font-weight:600;font-size:15px;color:var(--primary)">' + r.name + '</div>' +
            '<div style="display:flex;gap:6px;align-items:center;flex-wrap:wrap;margin-top:2px">' +
              '<span style="font-size:12px;color:var(--neutral-lighter)">' + r.chamber + '</span>' +
              '<span class="rep-badge" style="background:' + partyColor + '15;color:' + partyColor + ';padding:1px 6px;border-radius:3px;font-size:10px;font-weight:600">' + (r.party ? r.party.substring(0,4) : '') + '</span>' +
              '<span class="level-badge" style="background:' + levelColor + '15;color:' + levelColor + ';padding:1px 5px;border-radius:3px;font-size:9px;font-weight:600">' + levelBadge + '</span>' +
              '<span style="font-size:11px;color:var(--neutral-light)">' + r.state + (r.district ? ' - District ' + r.district : '') + '</span>' +
            '</div>' +
            roleHtml +
          '</div>' +
        '</div>' +
        bioHtml +
        lifeHtml +
        '<div style="display:flex;gap:6px;margin-top:10px;flex-wrap:wrap">' +
          phoneHtml +
          webHtml +
        '</div>' +
      '</div>';
    }).join('');
  }
  var emptyHtml = '<div class="card" style="text-align:center;padding:24px;color:var(--neutral-lighter)">' +
    '<div style="display:flex;flex-direction:column;align-items:center;gap:8px">' +
      '<i data-lucide="map-pin" style="width:32px;height:32px;opacity:0.3"></i>' +
      '<p>Enter your ZIP in Settings to see your reps.</p>' +
      '<button class="btn btn-primary" onclick="navigate(\'settings\')">Enter ZIP Code</button>' +
    '</div>' +
  '</div>';
  var fedCount = state.reps.filter(function(r) { return r.level === 'federal'; }).length;
  var stateCount = state.reps.filter(function(r) { return r.level === 'state'; }).length;
  return '<div class="dashboard-greeting">' +
    '<h2>Welcome' + (state.address ? ', Neighbor' : '') + '</h2>' +
    '<p>See every person representing you.</p>' +
    addressChip +
  '</div>' +
  '<div class="section">' +
    '<div class="section-header"><h3>Your Representatives</h3></div>' +
    (repCards || emptyHtml) +
  '</div>' +
  '<div id="coverage-note" style="margin-top:12px;display:none"></div>' +
  '<div class="tracking-stats">' +
    '<div class="stat-card"><div class="stat-number">' + state.reps.length + '</div><div class="stat-label">Officials Found</div></div>' +
    '<div class="stat-card"><div class="stat-number">' + fedCount + '</div><div class="stat-label">Federal</div></div>' +
    '<div class="stat-card"><div class="stat-number">' + stateCount + '</div><div class="stat-label">State</div></div>' +
  '</div>';
}

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

function renderSettings() {
  return '<div class="settings-section">' +
    '<h4>Location</h4>' +
    '<div class="card" style="padding:16px">' +
      '<div style="display:flex;gap:8px">' +
        '<input type="text" class="input-field" id="settings-zip" placeholder="Enter your ZIP code" value="' + (state.zipCode || '') + '" style="flex:1" maxlength="10" />' +
        '<button class="btn btn-primary" id="settings-find-reps">Find Reps</button>' +
      '</div>' +
      (state.address ? '<div class="address-chip" style="margin-top:8px"><i data-lucide="map-pin" style="width:14px;height:14px"></i> ' + state.address + '</div>' : '') +
      '<p style="font-size:11px;color:var(--neutral-lighter);margin-top:8px">ZIP code is used to find your federal and state representatives via public APIs.</p>' +
    '</div>' +
  '</div>' +
  '<div class="settings-section">' +
    '<h4>About This App</h4>' +
    '<div class="card" style="padding:16px">' +
      '<p style="font-size:13px;color:var(--neutral);line-height:1.6;margin-bottom:12px">Civic Sidekick helps you find and learn about every elected official who represents you. Enter your ZIP code to see your federal and state representatives, with educational information about what each office does and how it affects your daily life.</p>' +
      '<div class="privacy-notice">' +
        '<i data-lucide="shield" style="width:18px;height:18px;color:var(--primary);flex-shrink:0;margin-top:2px"></i>' +
        '<p><strong>Your privacy matters.</strong> Your ZIP code is only used to find your representatives via public APIs. No data is stored on our servers.</p>' +
      '</div>' +
    '</div>' +
  '</div>' +
  '<div style="text-align:center;margin-top:16px">' +
    '<p style="font-size:12px;color:var(--neutral-lighter)">Civic Sidekick v2.0 &middot; Data from ProPublica, OpenStates, Google Civic &amp; Wikipedia</p>' +
  '</div>';
}

// --- Toast notification ---
function showToast(msg) {
  var existing = document.getElementById('toast');
  if (existing) existing.remove();
  var toast = document.createElement('div');
  toast.id = 'toast';
  toast.style.cssText = 'position:fixed;bottom:80px;left:50%;transform:translateX(-50%);background:#001f45;color:white;padding:10px 20px;border-radius:8px;font-size:13px;font-weight:500;z-index:9997;box-shadow:0 4px 12px rgba(0,0,0,0.15);animation:slideUp 0.3s ease';
  toast.textContent = msg;
  document.body.appendChild(toast);
  setTimeout(function() {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s';
    setTimeout(function() { toast.remove(); }, 300);
  }, 3000);
}

// ============================================
// EVENT LISTENERS / ACTIONS
// ============================================

function attachScreenListeners(screenId) {
  // Landing page ZIP search
  var landingBtn = document.getElementById('landing-go');
  var landingInput = document.getElementById('landing-zip');
  if (landingBtn && landingInput) {
    var doSearch = function() {
      var zip = landingInput.value.trim();
      if (zip.match(/^\d{5}(-\d{4})?$/)) {
        state.address = zip;
        findReps(zip);
      } else {
        showError('Please enter a valid 5-digit ZIP code.');
      }
    };
    landingBtn.addEventListener('click', doSearch);
    landingInput.addEventListener('keydown', function(e) { if (e.key === 'Enter') doSearch(); });
  }

  // Settings page ZIP search
  var settingsBtn = document.getElementById('settings-find-reps');
  var settingsInput = document.getElementById('settings-zip');
  if (settingsBtn && settingsInput) {
    var doSettingsSearch = function() {
      var zip = settingsInput.value.trim();
      if (zip.match(/^\d{5}(-\d{4})?$/)) {
        state.address = zip;
        state.zipCode = zip;
        findReps(zip);
      } else {
        showError('Please enter a valid 5-digit ZIP code.');
      }
    };
    settingsBtn.addEventListener('click', doSettingsSearch);
    settingsInput.addEventListener('keydown', function(e) { if (e.key === 'Enter') doSettingsSearch(); });
  }

  // Nav items
  document.querySelectorAll('.nav-item').forEach(function(el) {
    el.addEventListener('click', function() {
      var screen = el.dataset.screen;
      navigate(screen);
    });
  });
}

// ============================================
// INIT
// ============================================

document.addEventListener('DOMContentLoaded', function() {
  renderApp();
});
