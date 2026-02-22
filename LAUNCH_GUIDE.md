# VoiceNote Launch Guide: 0 → 100 Users → Revenue

## Current State of Your Kotlin App (Honest Assessment)

Your app is **70% done**. Most screens exist and look good. Here's what's actually working vs what needs work:

| Feature | UI Built | Wired to Backend | Status |
|---------|----------|-------------------|--------|
| Login/Register | Yes | Yes | WORKING |
| Voice Recording | Yes | Yes | WORKING |
| Audio Upload + Processing | Yes | Yes | WORKING |
| Notes List (Vault) | Yes | Yes | WORKING |
| Note Detail + Transcript | Yes | Yes | WORKING |
| Task Center | Yes | Yes | WORKING |
| Task Toggle Complete | Yes | Yes | WORKING |
| Dashboard Metrics | Yes | Yes | WORKING |
| Search | Yes | Yes | WORKING |
| AI Chat (Global) | Yes | Yes | WORKING |
| Audio Player | Yes | Yes | WORKING |
| Biometric Lock | Yes | Yes | WORKING |
| SSE Real-time Updates | Yes | Yes | WORKING |
| Notifications | Yes | Local only | PARTIAL |
| Settings/Profile | Yes | Partially | PARTIAL |
| **Task Suggested Actions** | **NO** | Model exists | **MISSING** |
| **Chat with specific Note** | **NO** | API exists | **MISSING** |
| **Refresh Token** | **NO** | API exists | **MISSING (CRITICAL)** |
| **Role Selection → Backend** | UI exists | Not wired | **MISSING** |
| **Google Play Billing** | Hardcoded UI | Not started | **MISSING** |
| **Onboarding Flow** | Skeleton | Not wired | **MISSING** |
| Folders | NO | API exists | NOT STARTED |
| Offline Queue | Partial | Partial | INCOMPLETE |

### Critical Bug: No Refresh Token

Your `TokenManager` only stores `access_token`. After 60 minutes, the token expires and the user gets logged out with no recovery. The backend returns `refresh_token` on login but the app ignores it.

---

## Phase 1: Launch-Ready MVP (Target: 2 weeks)

**Goal: App that a real person can use daily at work**

### P1.1 — Fix Refresh Token (Day 1) [CRITICAL]

**File:** `data/local/TokenManager.kt`

What to add:
- Store `refresh_token` alongside `access_token`
- Add `saveRefreshToken()` / `getRefreshToken()` methods

**File:** `core/network/AuthInterceptor.kt`

What to add:
- On 401 response → call `POST /api/v1/users/refresh` with saved refresh_token
- Save new access_token
- Retry the failed request
- If refresh also fails → navigate to login

This is the #1 priority. Without it, users lose their session every hour.

### P1.2 — Task Suggested Actions UI (Day 2-4) [YOUR DIFFERENTIATOR]

The backend already returns `SuggestedActions` on every task with:
```json
{
  "google_search": {"query": "...", "reason": "..."},
  "email": {"to": "...", "subject": "...", "body": "..."},
  "whatsapp": {"phone": "...", "message": "..."},
  "ai_prompt": {"prompt": "...", "context": "..."}
}
```

The Kotlin model `SuggestedActions` already parses this. You need UI.

**New file:** `features/tasks/components/SuggestedActionsBar.kt`

```
┌──────────────────────────────────────┐
│ Task: "Schedule meeting with Ahmed"  │
│ Priority: HIGH | Deadline: Tomorrow  │
│                                      │
│ ┌─────────┐ ┌──────┐ ┌──────────┐  │
│ │ Google  │ │Email │ │ AI Prompt│  │
│ │ Search  │ │Draft │ │  Ask AI  │  │
│ └─────────┘ └──────┘ └──────────┘  │
│ ┌──────────┐ ┌──────────────────┐   │
│ │ WhatsApp │ │  Open in Maps   │   │
│ └──────────┘ └──────────────────┘   │
└──────────────────────────────────────┘
```

Each button:
- **Google Search** → Opens Chrome with the suggested query
- **Email** → Opens email intent with pre-filled to/subject/body
- **WhatsApp** → Opens WhatsApp with pre-filled message
- **AI Prompt** → Opens AI Chat with the suggested prompt pre-loaded
- **Maps** → Opens Google Maps with the location

**Where to add it:** Inside `TaskItem` composable in `TaskCenterScreen.kt` — expand the card to show actions when tapped.

### P1.3 — Chat with This Note (Day 3-4)

**Current state:** AI Chat exists but it's global (asks about ALL notes).
**What users want:** "Chat with THIS specific note" — ask questions about one recording.

The backend already has: `POST /api/v1/notes/{note_id}/ask` with body `{"question": "..."}`.

**Changes needed:**

1. **`NoteDetailScreen.kt`** — Add a "Chat" tab (4th tab alongside Transcript, Insights, Tasks)
2. **`NoteDetailViewModel.kt`** — Add `askNoteQuestion(noteId, question)` method
3. **`NotesApi.kt`** — Already has the endpoint? Check. If not, add:
   ```kotlin
   @POST("api/v1/notes/{id}/ask")
   suspend fun askAboutNote(@Path("id") id: String, @Body body: Map<String, String>): Response<AiAskResponse>
   ```
4. The Chat tab reuses the `ChatBubble` composable from `AIChatScreen.kt`

### P1.4 — Wire Role Selection (Day 5)

Role selection screen exists but doesn't send the choice to the backend. This is important because the AI personalizes summaries based on role (STUDENT gets lecture notes, OFFICE_WORKER gets meeting minutes).

**File:** `features/auth/RoleSelectionScreen.kt`

Wire the selected role to:
```
PATCH /api/v1/users/me  {"primary_role": "STUDENT"}
```

Roles: STUDENT, TEACHER, DEVELOPER, OFFICE_WORKER, BUSINESS_MAN, PSYCHIATRIST, PSYCHOLOGIST, GENERIC

### P1.5 — Folders (Day 6-7)

Users need to organize notes. Backend CRUD is complete.

**New files needed:**
- `features/folders/FolderListScreen.kt` — Grid of colored folders
- `features/folders/FolderViewModel.kt` — CRUD operations
- `data/api/FoldersApi.kt`:
  ```kotlin
  @GET("api/v1/folders") suspend fun getFolders(): Response<List<FolderResponse>>
  @POST("api/v1/folders") suspend fun createFolder(@Body body: Map<String, String>): Response<FolderResponse>
  @DELETE("api/v1/folders/{id}") suspend fun deleteFolder(@Path("id") id: String): Response<Unit>
  ```
- Add folder filter to VaultScreen (notes list)
- "Move to folder" action on NoteDetail

### P1.6 — Clean Onboarding (Day 8)

Flow: Welcome → Login/Register → Role Selection → Permissions → Dashboard

Wire the existing skeleton screens into a working flow:
1. `WelcomeScreen` → show app value prop, "Get Started" button
2. `LoginScreen` → existing, works
3. `RoleSelectionScreen` → wire to PATCH /users/me (P1.4)
4. `PermissionsGuideScreen` → request mic + notification permissions
5. → Dashboard

### P1.7 — Final Polish (Day 9-10)

- Test full flow on a real Android device (not just emulator)
- Fix any crashes from the above changes
- Test with real voice recordings (meetings, lectures)
- Make sure SSE updates refresh the note when processing finishes
- Test offline → online sync
- Remove all hardcoded sample data

---

## Phase 2: Monetization — Google Play Billing (Target: Week 3-4)

### P2.1 — Subscription Tiers

```
FREE TIER (default on register):
├── 10 voice notes per month
├── Basic transcription (1 STT engine)
├── 20 tasks max active
├── Standard AI model for summaries
├── No semantic search
└── Ads (optional — or just limit features)

PRO TIER ($4.99/month or $39.99/year):
├── Unlimited voice notes
├── Premium transcription (dual STT with failover)
├── Unlimited tasks
├── Better AI model (70B parameter)
├── Semantic search across all notes
├── Chat with individual notes
├── Task suggested actions (Google, Email, WhatsApp, AI)
├── Role-based AI personalization
├── Folders
├── Priority support
└── No ads
```

**Why $4.99/month:** Low enough for individual users, high enough to cover API costs. Most voice note apps charge $6-10/month.

**Why these tier limits:** Free tier gives enough value to get hooked (10 notes is 2-3 per week). Pro unlocks everything that makes the app truly useful.

### P2.2 — Google Play Billing Implementation

**IMPORTANT:** Android apps MUST use Google Play Billing for in-app purchases. Using Stripe directly in a mobile app violates Google Play policy and will get you banned.

**Dependencies to add (`build.gradle.kts`):**
```kotlin
implementation("com.android.billingclient:billing-ktx:7.1.1")
```

**New files:**
```
data/billing/
├── BillingManager.kt          // Google Play Billing client wrapper
├── SubscriptionRepository.kt  // Manage subscription state
└── PurchaseVerifier.kt        // Verify with your backend
```

**Flow:**
1. User taps "Upgrade to Pro" in app
2. `BillingManager` shows Google Play purchase dialog
3. User pays via Google (their Google account payment method)
4. Google sends purchase token to your app
5. App sends token to your backend: `POST /api/v1/webhooks/play-billing`
6. Backend verifies with Google Play API
7. Backend updates user tier to PREMIUM
8. App refreshes user profile → Pro features unlocked

**Backend changes needed:**
- New endpoint: `POST /api/v1/webhooks/play-billing` (verify purchase token)
- Store Google Play subscription ID on User model
- Check tier on feature-gated endpoints (already done with `requires_tier`)

### P2.3 — Replace Hardcoded Billing Screen

Current `WalletBillingScreen.kt` is 100% hardcoded ("2,450 credits", "$299 Enterprise Pro"). Replace with:

- Real subscription status from user profile
- "Upgrade to Pro" button that triggers BillingManager
- "Manage Subscription" → deep link to Google Play subscription management
- Real credit balance from `GET /api/v1/users/balance`
- Remove "Enterprise" language (you're targeting individuals)

### P2.4 — Usage Tracking (Backend)

Track monthly usage per user to enforce free tier limits:

```python
# In note processing endpoint, before processing:
if user.tier == FREE:
    monthly_count = count_notes_this_month(db, user.id)
    if monthly_count >= 10:
        raise HTTPException(402, "Free tier limit: 10 notes/month. Upgrade to Pro for unlimited.")
```

---

## Phase 3: Growth — 0 to 100 Users (Target: Week 5-8)

### P3.1 — Play Store Listing

**App Name:** VoiceNote AI — Smart Voice Notes & Tasks
**Short Description:** Record meetings, get transcripts, summaries & tasks automatically.
**Category:** Productivity
**Target:** 13+ (no mature content)

**Screenshots needed (5-8):**
1. Recording screen with waveform
2. Note detail with transcript + summary
3. Task center with suggested actions
4. AI chat conversation
5. Dashboard metrics

**Feature graphic:** Dark themed, show microphone → AI → organized notes

### P3.2 — Admin Dashboard Usage

Your Next.js admin dashboard is already built. Use it to:
- Monitor active users
- Track which features are used most
- Watch for errors in note processing
- Manage API keys (rotate when rate limited)
- View billing/revenue

### P3.3 — Getting First 100 Users

**Week 1-2 after launch:**
1. Share with friends/colleagues (10-20 users)
2. Post in Reddit: r/productivity, r/androidapps, r/voicenotes
3. Post in X/Twitter with demo video (30 sec screen recording)
4. Post in LinkedIn (target office workers)
5. Ask first users for Play Store reviews

**Week 3-4:**
6. Product Hunt launch
7. Facebook groups for productivity
8. University student groups (WhatsApp/Telegram)
9. Local tech meetups

**Week 5-8:**
10. Iterate based on real feedback
11. Fix what users actually complain about
12. Add features users actually request

### P3.4 — Analytics to Track

Add Firebase Analytics or similar:
- `recording_started` — how many people record
- `note_processed` — how many complete the flow
- `task_action_clicked` — which suggested actions are used
- `subscription_started` — conversion rate
- `subscription_cancelled` — churn rate
- `daily_active_users` — engagement

---

## Feature Priority Matrix

| Feature | Revenue Impact | User Retention | Effort | Phase |
|---------|---------------|----------------|--------|-------|
| Refresh token fix | None (but app breaks without it) | CRITICAL | 1 day | P1 |
| Task suggested actions | HIGH (differentiator) | HIGH | 2 days | P1 |
| Chat with note | HIGH (daily use) | HIGH | 2 days | P1 |
| Role selection | MEDIUM (personalization) | MEDIUM | 0.5 day | P1 |
| Folders | MEDIUM (organization) | HIGH | 2 days | P1 |
| Google Play Billing | CRITICAL (revenue) | N/A | 4 days | P2 |
| Usage limits (free tier) | HIGH (drives upgrades) | N/A | 1 day | P2 |
| Play Store listing | CRITICAL (distribution) | N/A | 1 day | P3 |
| Push notifications | MEDIUM | HIGH | 2 days | P3 |
| Offline mode | LOW | MEDIUM | 3 days | P3 |
| Teams/collaboration | LOW (B2B only) | LOW | SKIP |  |
| Enterprise admin | LOW | LOW | SKIP | |
| Integrations | LOW | LOW | SKIP | |

---

## What NOT to Build (Save for Later)

| Feature | Why Skip |
|---------|----------|
| Teams/Collaboration | Need 1000+ users before B2B makes sense |
| Enterprise SSO | No enterprises are asking for it |
| Integrations (Google, Notion) | Users don't use integrations as much as you think |
| Organization/B2B | Premature until you validate individual market |
| Real-time WebSocket editing | Over-engineered for single-user notes |
| Speaker diarization | Nice-to-have, not critical |
| Geofencing/work locations | Nobody will use this |
| Conflict detection | Single user = no conflicts |
| Productivity reports | Users don't read reports |
| OpenTelemetry/Prometheus | You'll have <100 users, check logs manually |

---

## Cost Analysis: Can You Afford to Run This?

### Monthly costs at 100 users:

| Service | Cost | Notes |
|---------|------|-------|
| Groq API (STT) | $0 - $5 | Free tier covers ~600 audio minutes/month |
| Groq API (LLM) | $0 - $5 | Free tier covers significant usage |
| Deepgram (fallback) | $0 | Only used when Groq fails |
| VPS (DigitalOcean/Hetzner) | $20-40 | 4GB RAM, 2 CPU sufficient |
| PostgreSQL | $0 | Runs on same VPS |
| Redis | $0 | Runs on same VPS |
| Domain + SSL | $12/year | Cloudflare free SSL |
| Google Play Developer | $25 one-time | One-time fee |
| **Total** | **~$30-50/month** | |

### Revenue at 100 users (10% conversion):
- 10 Pro subscribers × $4.99 × 70% (Google's cut) = **$34.93/month**
- Breakeven at ~15 paying subscribers

### Revenue at 1000 users (10% conversion):
- 100 Pro subscribers × $4.99 × 70% = **$349/month**

---

## Your Weekly Schedule

### Week 1: Fix + Build Core
- Mon: P1.1 (Refresh token) + P1.4 (Role selection)
- Tue-Wed: P1.2 (Task suggested actions UI)
- Thu: P1.3 (Chat with note)
- Fri: P1.5 (Folders - start)

### Week 2: Folders + Polish
- Mon: P1.5 (Folders - finish)
- Tue: P1.6 (Onboarding flow)
- Wed-Thu: P1.7 (Testing on real device, fix crashes)
- Fri: Buffer for overflow

### Week 3: Billing
- Mon-Tue: P2.1 + P2.2 (Google Play Billing)
- Wed: P2.3 (Real billing screen)
- Thu: P2.4 (Usage tracking backend)
- Fri: Test billing end-to-end

### Week 4: Launch
- Mon: P3.1 (Play Store listing, screenshots)
- Tue: Submit to Play Store review
- Wed-Thu: Marketing prep (Reddit posts, demo video)
- Fri: Launch day (or whenever approved)

### Week 5-8: Grow
- Fix bugs users report
- Add features users request
- Market consistently
- Track analytics
- Iterate

---

## One Rule to Follow

**Every feature you add must answer: "Will this get me closer to 100 paying users?"**

- Refresh token? Yes (users can't pay if they keep getting logged out)
- Task actions? Yes (this is why they'll choose your app over competitors)
- Chat with note? Yes (daily engagement → conversion)
- Billing? Yes (literally how you get paid)
- Teams? No (not yet)
- Enterprise? No (not yet)
- Integrations? No (not yet)

Stick to this filter. Ship fast. Iterate based on real users.
