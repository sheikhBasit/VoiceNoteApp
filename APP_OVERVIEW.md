# VoiceNote Enterprise - Technical Overview

VoiceNote is a high-fidelity, enterprise-grade AI productivity application built with Jetpack Compose. It transforms audio capture into actionable intelligence through a modular, offline-first native Android architecture.

## 🚀 Vision
To empower enterprise teams with real-time AI insights, seamless transcription, and automated task extraction, all while maintaining strict corporate security and offline reliability.

## 🏗️ Architectural Foundations

The application is built on **Clean Architecture** principles with a focus on modularity and testability.

### Layered Structure
- **UI Layer (Jetpack Compose)**: Uses Unidirectional Data Flow (UDF) via Hilt-injected ViewModels.
- **Domain Layer**: Contains business logic, use cases, and repository interfaces.
- **Data Layer (Offline-First)**: 
    - **Room Persistence**: Local cache for notes, tasks, and notifications.
    - **FileSystem**: Secure storage for high-quality audio recordings.
    - **Retrofit Client**: Direct integration with the `VoiceNoteAPI`.
    - **SSE Client**: Server-Sent Events for real-time processing updates and collision alerts.

## 🛠️ Feature Modules

### 🔐 Authentication & Onboarding
- **Enterprise Portal**: Corporate-specific login with SSO (Azure AD, Google) support.
- **Workspace Setup**: Deep-linkable organization joining and role-based onboarding (Manager, Developer, Sales, Executive) to tailor AI insights.
- **Permission Guard**: iOS-style granular permission requested with context-aware benefits.

### 📊 Intelligence Dashboard
- **Velocity Tracking**: ROI and productivity analytics using usage metering.
- **Heatmaps**: Visualizing activity density across the team.
- **Real-time Status**: SSE-driven updates showing "Processing", "Synced", or "Conflict Detected".

### 📂 The Vault (Knowledge Management)
- **Note Repository**: Hierarchical management of meeting notes and voice memos.
- **Note Insights**: AI-generated Executive Summaries, Key Takeaways, and Sentiment Analysis.
- **RAG Chat**: A global AI assistant capable of answering questions across the entire knowledge base (Cross-Note RAG).

### 📋 Actionable Task Center
- **System Integration**: Automated sync with System Calendar and Contacts.
- **Smart Actions**: One-tap triggers for WhatsApp messages, Map navigation, and Email follows based on transcribed action items.

### 🎙️ Advanced Voice Capture
- **Diarization Engine**: UI support for multi-speaker identification and labeling.
- **Local Waveform**: Real-time visualization of audio frequency during recording.
- **Batch Sync**: Background optimization for uploading large audio files to the cloud.

## 🎨 Design System & Aesthetics

### Visual Language
- **Premium Dark Mode**: HSL-tailored color palette with vibrants like `PrimaryBlue` (#0d6cf2).
- **Glassmorphism**: iOS-style blur effects on headers and bottom sheets.
- **Micro-Animations**: Shimmering skeletal states, pulse effects for AI processing, and fluid layout transitions.

### Centralized Core UI
All components are atomic and reusable:
- `ActionConfirmationSheet`: Standardized destructive action flow.
- `ExportOptionsSheet`: Multi-format portability (PDF, MD, Notion, Slack).
- `ConnectivityAlert`: Graceful degradation for offline states.
- `SystemError`: Advanced recovery paths with specific node/error code metadata.

## 📱 Screen Inventory (24 High-Fidelity Views)
1.  **Welcome**
2.  **Enterprise Sign-In**
3.  **Select Your Role**
4.  **Workspace Setup**
5.  **Access Permissions Guide**
6.  **Intelligence Dashboard**
7.  **The Vault**
8.  **Note Insights View**
9.  **Actionable Task Center**
10. **Voice Capture Engine**
11. **AI Processing State**
12. **Meeting Mode Record**
13. **AI Customization Settings**
14. **Enterprise Admin Control**
15. **Folder Access Control**
16. **Notification Center**
17. **User Profile & Stats**
18. **Help & Support**
19. **Wallet & Billing**
20. **Connectivity Alert**
21. **System Error**
22. **Export Options**
23. **Action Confirmation**
24. **Empty Vault State**

## ✅ Quality Assurance
- **Strict Parade Parity**: 1:1 match with provided high-fidelity HTML/CSS mocks.
- **Zero Dummy Data**: Models are strictly aligned with backend schemas.
- **Performance**: Optimized for large audio processing and real-time UI reactive states.
