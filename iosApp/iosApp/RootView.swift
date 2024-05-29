import UIKit
import SwiftUI
import ComposeApp

struct RootView: UIViewControllerRepresentable {
    let component: RootComponent
    let backDispatcher: BackDispatcher

    func makeUIViewController(context: Context) -> UIViewController {
        return RootViewControllerKt.rootViewController(component: component, backDispatcher: backDispatcher)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
