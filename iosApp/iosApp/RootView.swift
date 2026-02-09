import ComposeApp
import SwiftUI
import UIKit

struct RootView: UIViewControllerRepresentable {
    let component: RootComponent
    let backDispatcher: BackDispatcher

    func makeUIViewController(context _: Context) -> UIViewController {
        return RootViewControllerKt.rootViewController(component: component, backDispatcher: backDispatcher)
    }

    func updateUIViewController(_: UIViewController, context _: Context) {}
}
