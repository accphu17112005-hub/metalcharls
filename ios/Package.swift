// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "MetalScope",
    platforms: [
        .iOS(.v17),
        .macOS(.v14)
    ],
    products: [
        .library(
            name: "MetalScopeLib",
            targets: ["MetalScope"]
        ),
    ],
    dependencies: [],
    targets: [
        .target(
            name: "MetalScope",
            dependencies: [],
            path: "MetalScope",
            exclude: ["Resources/Info.plist", "Tests"]
        ),
        .testTarget(
            name: "MetalScopeTests",
            dependencies: ["MetalScope"],
            path: "MetalScope/Tests"
        ),
    ]
)
