import Foundation

enum TimeRange: String, CaseIterable, Identifiable, Codable {
    case oneDay = "1D"
    case oneWeek = "1W"
    case oneMonth = "1M"
    case threeMonths = "3M"
    case sixMonths = "6M"
    case oneYear = "1Y"
    case all = "ALL"
    
    var id: String { rawValue }
    
    var label: String { rawValue }
    
    var description: String {
        switch self {
        case .oneDay: return "24 Hours (Intraday)"
        case .oneWeek: return "7 Days"
        case .oneMonth: return "30 Days"
        case .threeMonths: return "90 Days (Quarterly)"
        case .sixMonths: return "180 Days"
        case .oneYear: return "365 Days (Annual)"
        case .all: return "5-Year Macro History"
        }
    }
    
    var pointCount: Int {
        switch self {
        case .oneDay: return 24
        case .oneWeek: return 28
        case .oneMonth: return 30
        case .threeMonths: return 36
        case .sixMonths: return 40
        case .oneYear: return 52
        case .all: return 60
        }
    }
    
    static func from(label: String) -> TimeRange {
        let clean = label.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        return TimeRange.allCases.first { $0.label == clean } ?? .oneDay
    }
}
