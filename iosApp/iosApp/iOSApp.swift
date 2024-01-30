import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        PlatformKoinInitializer.init().invoke()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
