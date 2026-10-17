import Foundation
import Combine

class PreferencesRepository: ObservableObject {
    private let defaults = UserDefaults.standard
    
    private enum Keys {
        static let currency = "pref_currency"
        static let weightUnit = "pref_weight_unit"
        static let themeMode = "pref_theme_mode"
        static let hapticsEnabled = "pref_haptics"
        static let refreshInterval = "pref_refresh_interval"
        static let decimalOverride = "pref_decimal_override"
        static let isCardView = "pref_card_view"
    }
    
    @Published var currency: Currency {
        didSet {
            defaults.set(currency.code, forKey: Keys.currency)
        }
    }
    
    @Published var weightUnit: WeightUnit {
        didSet {
            defaults.set(weightUnit.code, forKey: Keys.weightUnit)
        }
    }
    
    @Published var themeMode: AppThemeMode {
        didSet {
            defaults.set(themeMode.rawValue, forKey: Keys.themeMode)
        }
    }
    
    @Published var isHapticsEnabled: Bool {
        didSet {
            defaults.set(isHapticsEnabled, forKey: Keys.hapticsEnabled)
        }
    }
    
    @Published var refreshInterval: Int {
        didSet {
            defaults.set(refreshInterval, forKey: Keys.refreshInterval)
        }
    }
    
    @Published var decimalOverride: Int? {
        didSet {
            if let val = decimalOverride {
                defaults.set(val, forKey: Keys.decimalOverride)
            } else {
                defaults.removeObject(forKey: Keys.decimalOverride)
            }
        }
    }
    
    @Published var isCardView: Bool {
        didSet {
            defaults.set(isCardView, forKey: Keys.isCardView)
        }
    }
    
    init() {
        let curCode = defaults.string(forKey: Keys.currency) ?? Currency.usd.code
        self.currency = Currency.from(code: curCode)
        
        let unitCode = defaults.string(forKey: Keys.weightUnit) ?? WeightUnit.oz.code
        self.weightUnit = WeightUnit.from(code: unitCode)
        
        let themeRaw = defaults.string(forKey: Keys.themeMode) ?? AppThemeMode.dark.rawValue
        self.themeMode = AppThemeMode(rawValue: themeRaw) ?? .dark
        
        self.isHapticsEnabled = defaults.object(forKey: Keys.hapticsEnabled) as? Bool ?? true
        self.refreshInterval = defaults.object(forKey: Keys.refreshInterval) as? Int ?? 15
        self.decimalOverride = defaults.object(forKey: Keys.decimalOverride) as? Int
        self.isCardView = defaults.bool(forKey: Keys.isCardView)
    }
    
    func clearAll() {
        defaults.removeObject(forKey: Keys.currency)
        defaults.removeObject(forKey: Keys.weightUnit)
        defaults.removeObject(forKey: Keys.themeMode)
        defaults.removeObject(forKey: Keys.hapticsEnabled)
        defaults.removeObject(forKey: Keys.refreshInterval)
        defaults.removeObject(forKey: Keys.decimalOverride)
        defaults.removeObject(forKey: Keys.isCardView)
        
        self.currency = .usd
        self.weightUnit = .oz
        self.themeMode = .dark
        self.isHapticsEnabled = true
        self.refreshInterval = 15
        self.decimalOverride = nil
        self.isCardView = false
    }
}
