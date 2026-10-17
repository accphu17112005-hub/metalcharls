import SwiftUI

// MARK: - MetalScope Color Palette Tokens (Matching Android theme)

extension Color {
    // Dark Palette (Primary Design)
    static let metalBackgroundDark = Color(red: 0x0B / 255.0, green: 0x10 / 255.0, blue: 0x20 / 255.0)
    static let metalSurfaceDark = Color(red: 0x12 / 255.0, green: 0x1A / 255.0, blue: 0x2B / 255.0)
    static let metalElevatedDark = Color(red: 0x19 / 255.0, green: 0x23 / 255.0, blue: 0x38 / 255.0)
    static let metalBorderDark = Color(red: 0x28 / 255.0, green: 0x34 / 255.0, blue: 0x49 / 255.0)
    static let metalTextPrimaryDark = Color(red: 0xF4 / 255.0, green: 0xF7 / 255.0, blue: 0xFC / 255.0)
    static let metalTextSecondaryDark = Color(red: 0x96 / 255.0, green: 0xA3 / 255.0, blue: 0xB8 / 255.0)
    static let metalTextMutedDark = Color(red: 0x64 / 255.0, green: 0x74 / 255.0, blue: 0x8B / 255.0)

    // Brand & Accent Colors
    static let metalGold = Color(red: 0xD9 / 255.0, green: 0xB4 / 255.0, blue: 0x5B / 255.0)
    static let metalGoldDark = Color(red: 0xB8 / 255.0, green: 0x92 / 255.0, blue: 0x38 / 255.0)
    static let metalGoldLight = Color(red: 0xF3 / 255.0, green: 0xD2 / 255.0, blue: 0x80 / 255.0)

    static let metalSilver = Color(red: 0xB8 / 255.0, green: 0xC4 / 255.0, blue: 0xD6 / 255.0)
    static let metalPlatinum = Color(red: 0xA5 / 255.0, green: 0xB4 / 255.0, blue: 0xCB / 255.0)
    static let metalPalladium = Color(red: 0xC7 / 255.0, green: 0xCB / 255.0, blue: 0xD1 / 255.0)

    // Financial Indicators
    static let changePositive = Color(red: 0x22 / 255.0, green: 0xC5 / 255.0, blue: 0x5E / 255.0)
    static let changeNegative = Color(red: 0xEF / 255.0, green: 0x44 / 255.0, blue: 0x44 / 255.0)
    static let changeNeutral = Color(red: 0x94 / 255.0, green: 0xA3 / 255.0, blue: 0xB8 / 255.0)

    // Light Palette (Alternative)
    static let metalBackgroundLight = Color(red: 0xF8 / 255.0, green: 0xFA / 255.0, blue: 0xFC / 255.0)
    static let metalSurfaceLight = Color.white
    static let metalElevatedLight = Color(red: 0xF1 / 255.0, green: 0xF5 / 255.0, blue: 0xF9 / 255.0)
    static let metalBorderLight = Color(red: 0xE2 / 255.0, green: 0xE8 / 255.0, blue: 0xF0 / 255.0)
    static let metalTextPrimaryLight = Color(red: 0x0F / 255.0, green: 0x17 / 255.0, blue: 0x2A / 255.0)
    static let metalTextSecondaryLight = Color(red: 0x47 / 255.0, green: 0x55 / 255.0, blue: 0x69 / 255.0)
    static let metalTextMutedLight = Color(red: 0x94 / 255.0, green: 0xA3 / 255.0, blue: 0xB8 / 255.0)
}

struct MetalThemeColors {
    let background: Color
    let surface: Color
    let elevated: Color
    let border: Color
    let textPrimary: Color
    let textSecondary: Color
    let textMuted: Color
    let gold: Color
    let silver: Color
    let platinum: Color
    let palladium: Color
    let positive: Color
    let negative: Color
    let neutral: Color
    
    static let dark = MetalThemeColors(
        background: .metalBackgroundDark,
        surface: .metalSurfaceDark,
        elevated: .metalElevatedDark,
        border: .metalBorderDark,
        textPrimary: .metalTextPrimaryDark,
        textSecondary: .metalTextSecondaryDark,
        textMuted: .metalTextMutedDark,
        gold: .metalGold,
        silver: .metalSilver,
        platinum: .metalPlatinum,
        palladium: .metalPalladium,
        positive: .changePositive,
        negative: .changeNegative,
        neutral: .changeNeutral
    )
    
    static let light = MetalThemeColors(
        background: .metalBackgroundLight,
        surface: .metalSurfaceLight,
        elevated: .metalElevatedLight,
        border: .metalBorderLight,
        textPrimary: .metalTextPrimaryLight,
        textSecondary: .metalTextSecondaryLight,
        textMuted: .metalTextMutedLight,
        gold: .metalGoldDark,
        silver: Color(red: 0x64 / 255.0, green: 0x74 / 255.0, blue: 0x8B / 255.0),
        platinum: Color(red: 0x47 / 255.0, green: 0x55 / 255.0, blue: 0x69 / 255.0),
        palladium: Color(red: 0x47 / 255.0, green: 0x55 / 255.0, blue: 0x69 / 255.0),
        positive: .changePositive,
        negative: .changeNegative,
        neutral: .changeNeutral
    )
}
