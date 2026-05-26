// ============================================
// Civic Sidekick - Election Education Module
// ============================================
// Explains what each office does and how it affects
// the user's quality of life, incorporated into
// search results and the Elections screen.

const ELECTION_EDUCATION = {
  // ===== FEDERAL POSITIONS =====
  federal: {
    title: '\u{1F3DB}\uFE0F Federal Positions',
    subtitle: 'Represent you at the national level in Washington, D.C.',
    total: '542 federal offices',
    positions: [
      {
        title: 'President & Vice President',
        level: 'federal',
        chamber: 'Executive',
        howElected: 'Elected every 4 years by the Electoral College, based on your state\'s popular vote.',
        whatTheyDo: 'Sets national policy direction, signs or vetoes laws, oversees federal agencies (healthcare, education, environment, defense), appoints federal judges and Supreme Court justices, conducts foreign policy.',
        affectsYourLife: 'Your healthcare costs, student loan policies, tax rates, environmental regulations, national security, and Supreme Court decisions that affect your rights.',
        nextElection: 'November 2028',
        searchable: true,
        source: 'GovTrack (federal officials via API)'
      },
      {
        title: 'U.S. Senate',
        level: 'federal',
        chamber: 'U.S. Senate',
        howElected: 'Two per state (100 total). Six-year terms, with ~1/3 of seats up every 2 years.',
        whatTheyDo: 'Confirms federal judges and Supreme Court justices, approves treaties, confirms Cabinet secretaries, passes federal legislation on all national issues.',
        affectsYourLife: 'Confirms the judges who rule on your rights, approves the officials who run federal agencies (FDA, EPA, Dept of Education), passes laws on healthcare, immigration, and taxes.',
        districtNote: 'Statewide — you vote for both of your state\'s senators',
        nextElection: 'November 2026 (Class I & III), November 2028 (Class II)',
        searchable: true,
        source: 'GovTrack (always included in ZIP search)'
      },
      {
        title: 'U.S. House of Representatives',
        level: 'federal',
        chamber: 'U.S. House',
        howElected: '435 voting members. Number per state based on population. All seats up every 2 years.',
        whatTheyDo: 'Originates all revenue/tax bills, passes federal legislation, can impeach federal officials, represents local district interests in Congress.',
        affectsYourLife: 'Determines your federal income tax rates, funds local programs (roads, schools, infrastructure), passes laws on healthcare, minimum wage, and consumer protections.',
        districtNote: 'Based on your congressional district — we find yours by ZIP',
        nextElection: 'November 2026',
        searchable: true,
        source: 'GovTrack (always included in ZIP search)'
      }
    ],
    summaryNote: 'Federal officials are reliably found via the GovTrack API for any valid ZIP code. If no federal reps appear, the ZIP may be invalid or the API may be temporarily unavailable.'
  },

  // ===== STATE POSITIONS =====
  state: {
    title: '\u{1F3DB}\uFE0F State Positions',
    subtitle: 'State governments have significant power over daily life.',
    positions: [
      {
        title: 'Governor & Lt. Governor',
        level: 'state',
        chamber: 'Governor',
        howElected: 'Elected every 4 years (most states). Head of the state\'s executive branch.',
        whatTheyDo: 'Signs or vetoes state laws, manages state budget, oversees state agencies (education, transportation, health), appoints state judges (in many states), commands state National Guard.',
        affectsYourLife: 'Your state income/sales taxes, quality of public schools, road conditions, state health insurance rules, COVID/health emergency responses, state police and public safety.',
        searchable: true,
        source: 'Static lookup (maintained for all 50 states + DC)'
      },
      {
        title: 'State Executive Officials',
        level: 'state',
        chamber: 'State Executive',
        howElected: 'Varies by state — typically elected every 4 years.',
        whatTheyDo: {
          'Attorney General': 'Chief legal officer — enforces state laws, consumer protections, handles lawsuits against corporations.',
          'Secretary of State': 'Oversees elections, business registrations, vehicle/voter registration in some states.',
          'State Treasurer': 'Manages state investments, unclaimed property, college savings plans.',
          'Superintendent of Public Instruction': 'Oversees public K-12 education standards, funding, and curriculum.'
        },
        affectsYourLife: 'Attorney General: protects you from scams and price gouging. Secretary of State: ensures your vote is counted. Treasurer: manages your state\'s pension and college savings. Education head: sets your kids\' learning standards.',
        searchable: 'partial',
        source: 'Google Civic API (requires full street address)',
        limitationNote: 'These officials appear inconsistently across APIs. We show them when available via Google Civic with your full address.'
      },
      {
        title: 'State Legislature',
        level: 'state',
        chamber: 'State Legislature',
        howElected: 'You vote for representatives in your state\'s two chambers (State Senate and State House/Assembly) to make state laws.',
        whatTheyDo: 'Pass state laws on education, healthcare, transportation, criminal justice, taxation. Approve state budget. Can override governor vetoes. Confirm state agency heads and judges.',
        affectsYourLife: 'Sets your state income tax rate, funds your local schools and roads, determines criminal laws and sentencing, regulates rent and housing laws, sets minimum wage (above federal), defines voting districts.',
        districtNote: 'You may have both a State Senator and a State Representative.',
        searchable: 'limited',
        source: 'OpenStates API (fetches ~20 per state)',
        limitationNote: 'Some states have 100+ state legislators. We fetch the first ~20 and provide a link to OpenStates.org to find the rest.'
      },
      {
        title: 'State Judicial Seats',
        level: 'state',
        chamber: 'State Judiciary',
        howElected: 'Varies by state:',
        whatTheyDo: {
          'Partisan Elections': 'Judges appear with party affiliation (e.g., AL, TX, PA).',
          'Nonpartisan Elections': 'Judges appear without party label (e.g., MI, WA, OH).',
          'Retention Elections': 'Voters decide if appointed judges stay in office (e.g., IA, KS, CO).',
          'Appointed (no election)': 'Some states appoint judges without public vote (e.g., MA, NH).'
        },
        affectsYourLife: 'State judges rule on family law (divorce, custody), criminal trials, landlord-tenant disputes, personal injury lawsuits, and state constitutional rights.',
        searchable: false,
        source: 'Not available via current APIs',
        limitationNote: 'State judicial elections are not covered by any free API we currently use. Check your state\'s election website or Ballotpedia.org.'
      }
    ],
    summaryNote: 'State officials are partially searchable. Governors are always found. State legislators are fetched from OpenStates (limited to ~20). Executive officials and judges require a full street address.'
  },

  // ===== LOCAL POSITIONS =====
  local: {
    title: '\u{1F3D8}\uFE0F Local Positions',
    subtitle: 'Local officials have the most direct impact on your community\'s daily services.',
    positions: [
      {
        title: 'County-Level Offices',
        level: 'local',
        chamber: 'County',
        whatTheyDo: {
          'County Commissioner / Supervisor': 'Sets county budget, property tax rates, zoning laws, oversees county services.',
          'County Executive': 'County\'s chief administrator — manages county departments and budget.',
          'County Clerk': 'Manages records (marriage licenses, property deeds), administers elections.',
          'Sheriff': 'Chief law enforcement for unincorporated areas, runs county jails.',
          'District Attorney (DA)': 'Prosecutes criminal cases on behalf of the state/county.',
          'County Judge': 'Presides over county court cases (varies by state).',
          'County Treasurer / Assessor': 'Collects property taxes, assesses property values.'
        },
        affectsYourLife: 'Property tax rates affect your housing costs. Sheriff and DA determine local law enforcement priorities. Zoning decisions affect what gets built in your neighborhood. County budget funds parks, libraries, and health services.',
        searchable: 'partial',
        source: 'Google Civic API (requires full street address)',
        limitationNote: 'County offices vary by state. Google Civic covers many but not all. Some rural counties may have limited data.'
      },
      {
        title: 'Municipal / City Offices',
        level: 'local',
        chamber: 'City',
        whatTheyDo: {
          'Mayor': 'Chief executive of the city — proposes budget, oversees city departments, sets policy direction.',
          'City Council Member': 'Passes city ordinances, approves city budget, sets property tax rates, represents ward/district.',
          'City Clerk': 'Manages city records, elections, meeting minutes, business licenses.',
          'City Treasurer': 'Manages city finances, investments, and debt.',
          'City Attorney': 'Provides legal counsel to city government, prosecutes municipal code violations.'
        },
        affectsYourLife: 'City council decides local housing policies, business regulations, and public safety budgets. Mayor influences quality of city services (trash pickup, parks, police). City clerk runs your local elections.',
        searchable: 'partial',
        source: 'Google Civic API (requires full street address)',
        limitationNote: 'Google Civic covers larger cities well. Small towns may have limited or no data.'
      },
      {
        title: 'Special Districts',
        level: 'local',
        chamber: 'Special District',
        whatTheyDo: {
          'School Board Member': 'Sets education policy, hires/fires superintendent, approves curriculum and school budgets.',
          'Water / Utility District Board': 'Oversees water quality, rates, and infrastructure.',
          'Fire District Board': 'Manages fire protection services and emergency response.',
          'Park District Board': 'Oversees public parks, recreation programs, and community centers.',
          'Public Hospital Board': 'Governs public hospital operations and healthcare access.',
          'Library District Board': 'Oversees public library operations and funding.'
        },
        affectsYourLife: 'School board decides your kids\' curriculum and school funding. Water board affects your water bill and quality. Fire district determines emergency response times. Park board maintains your local green spaces.',
        searchable: false,
        source: 'Not available via current APIs',
        limitationNote: 'Special districts vary wildly by state and locality. Check your county election website for these races.'
      }
    ],
    summaryNote: 'Local officials provide the services you interact with daily — schools, water, police, fire, parks. We can find mayor, city council, and some county officials using Google Civic with your full street address. School board and special district officials often require checking your county election website.'
  },

  // ===== WHAT WE CAN vs CANNOT FIND =====
  coverage: {
    reliable: [
      'U.S. Senators — always found by state',
      'U.S. Representatives — always found by ZIP code',
      'Governor — always found (50 states + DC static data)',
      'State Legislators — partially found (up to ~20 via OpenStates API)'
    ],
    partial: [
      'Mayor — found with full street address via Google Civic',
      'City Council — found with full street address',
      'County Officials — partially found via Google Civic',
      'State Executive Officials (AG, Treasurer, etc.) — some coverage',
      'School Boards — some coverage in larger districts'
    ],
    notAvailable: [
      'State Judges — not covered by current APIs',
      'Special District officials (water, fire, park, library boards)',
      'Precinct-level positions',
      'Local party committee members',
      'Territorial officials (Guam, Puerto Rico, etc.)'
    ],
    fallbackMessage: 'For officials not listed, visit Ballotpedia.org, your state\'s election website, or your county elections office.'
  }
};

// ===== HELPERS =====
function getEducationForRep(rep) {
  if (!rep) return null;
  const chamber = rep.chamber || '';
  const level = rep.level || '';
  const title = rep.title || '';
  const name = rep.name || '';

  if (chamber === 'U.S. Senate') return ELECTION_EDUCATION.federal.positions[1];
  if (chamber === 'U.S. House') return ELECTION_EDUCATION.federal.positions[2];
  if (chamber === 'Governor' || title === 'Governor') return ELECTION_EDUCATION.state.positions[0];
  if (chamber === 'President') return ELECTION_EDUCATION.federal.positions[0];
  if (chamber === 'State Senate' || chamber === 'State House' || chamber === 'State Legislature') return ELECTION_EDUCATION.state.positions[2];
  if (chamber === 'State Executive') return ELECTION_EDUCATION.state.positions[1];
  if (chamber === 'State Judiciary' || title.includes('Judge') || title.includes('Justice')) return ELECTION_EDUCATION.state.positions[3];
  if (chamber === 'Mayor' || title.includes('Mayor')) {
    const cityPos = ELECTION_EDUCATION.local.positions[1];
    return { ...cityPos, whatTheyDo: { 'Mayor': cityPos.whatTheyDo['Mayor'] } };
  }
  if (chamber === 'City Council' || chamber === 'City Commission') {
    const cityPos = ELECTION_EDUCATION.local.positions[1];
    return { ...cityPos, whatTheyDo: { 'City Council Member': cityPos.whatTheyDo['City Council Member'] } };
  }
  if (chamber === 'Sheriff') return { ...ELECTION_EDUCATION.local.positions[0], whatTheyDo: { 'Sheriff': ELECTION_EDUCATION.local.positions[0].whatTheyDo['Sheriff'] } };
  if (chamber === 'County' || title.includes('County')) return ELECTION_EDUCATION.local.positions[0];
  if (chamber === 'School Board' || title.includes('School')) return { ...ELECTION_EDUCATION.local.positions[2], whatTheyDo: { 'School Board Member': ELECTION_EDUCATION.local.positions[2].whatTheyDo['School Board Member'] } };
  if (level === 'local') return ELECTION_EDUCATION.local.positions[1];
  return null;
}

function getRoleDescription(rep) {
  const edu = getEducationForRep(rep);
  if (!edu) return '';
  if (typeof edu.whatTheyDo === 'string') return edu.whatTheyDo;
  if (typeof edu.whatTheyDo === 'object' && !Array.isArray(edu.whatTheyDo)) {
    const title = rep.title || rep.officeName || '';
    const chamber = rep.chamber || '';
    if (edu.whatTheyDo[title]) return edu.whatTheyDo[title];
    if (edu.whatTheyDo[chamber]) return edu.whatTheyDo[chamber];
    return edu.affectsYourLife || '';
  }
  return edu.affectsYourLife || '';
}

function getLifeImpact(rep) {
  const edu = getEducationForRep(rep);
  return edu?.affectsYourLife || '';
}

function getCoverageExplanation(searchType) {
  const c = ELECTION_EDUCATION.coverage;
  const base = {
    summary: 'We use multiple public APIs to find your elected officials. Some offices are better covered than others.',
    found: c.reliable,
    partial: c.partial,
    notFound: c.notAvailable,
    whatToDo: c.fallbackMessage
  };
  if (searchType === 'zip') {
    return {
      summary: 'ZIP search reliably finds your federal officials, Governor, and some state legislators.',
      found: c.reliable,
      notFound: [
        'Mayor, City Council — need full street address',
        'County officials — need full street address',
        'State executive officials (AG, Treasurer) — need full address',
        'Judges — not available from our data sources'
      ],
      whatToDo: 'For local officials, enter your full street address in Settings or the Browse page.'
    };
  }
  if (searchType === 'address') {
    return {
      summary: 'Full address search finds federal, state, and local officials where available.',
      found: c.reliable.concat(c.partial),
      notFound: c.notAvailable,
      whatToDo: 'For officials still not found, check Ballotpedia.org or your county election website.'
    };
  }
  return base;
}

// Export
if (typeof window !== 'undefined') {
  window.ELECTION_EDUCATION = ELECTION_EDUCATION;
  window.getEducationForRep = getEducationForRep;
  window.getRoleDescription = getRoleDescription;
  window.getLifeImpact = getLifeImpact;
  window.getCoverageExplanation = getCoverageExplanation;
}