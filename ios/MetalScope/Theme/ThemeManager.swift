import SwiftUI

enum AppThemeMode: String, CaseIterable, Identifiable {
    case dark = "DARK"
    case light = "LIGHT"
    case system = "SYSTEM"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .dark: return "Dark (Default)"
        case .light: return "Light"
        case .system: return "System Default"
        }
    }
}

class ThemeManager: ObservableObject {
    @Published var themeMode: AppThemeMode = .dark
    
    var colors: MetalThemeColors {
        switch themeMode {
        case .dark:
            return .dark
        case .light:
            return .light
        case .system:
            // Match system preference via UITraitCollection or fallback
            return .dark
        }
    }
    
    var colorScheme: ColorScheme? {
        switch themeMode {
        case .dark: return .dark
        case .light: return .light
        case .system: return nil
        }
    }
}
