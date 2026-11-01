import Foundation

struct MarketSessionStatus {
    let isOpen: BooleanLiteralType
    let sessionTitle: String
    let sessionDesc: String
}

enum MarketSessionUtils {
    static func getMarketSessionStatus() -> MarketSessionStatus {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone(secondsFromGMT: 0) ?? .current
        let now = Date()
        let weekday = calendar.component(.weekday, from: now)
        let hour = calendar.component(.hour, from: now)

        let isWeekend = (weekday == 7) || (weekday == 1 && hour < 22)

        if isWeekend {
            return MarketSessionStatus(
                isOpen: false,
                sessionTitle: "Market Closed",
                sessionDesc: "Weekend trading pause. LBMA Friday close prices shown."
            )
        } else {
            let session: String
            switch hour {
            case 0...7:
                session = "Asian Session (Tokyo / Singapore)"
            case 8...12:
                session = "European Session (London LBMA Open)"
            case 13...16:
                session = "Peak Global Liquidity (London & NY COMEX Active)"
            case 17...21:
                session = "Americas Session (NY COMEX Active)"
            default:
                session = "Pacific Pre-Market Transition"
            }
            return MarketSessionStatus(
                isOpen: true,
                sessionTitle: "Markets Active",
                sessionDesc: session
            )
        }
    }
}
