import ComposeApp
import SwiftUI

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    var body: some Scene {
        WindowGroup {
            RootView(component: appDelegate.component, backDispatcher: appDelegate.backDispatcher)
                .ignoresSafeArea(.all)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    private let stateKeeper = StateKeeperDispatcherKt.StateKeeperDispatcher(savedState: nil)
    let backDispatcher: BackDispatcher = BackDispatcherKt.BackDispatcher()

    lazy var component: RootComponent = {
        LogConfigKt.doInitNapierDebug()
        PlatformKoinInitializer().invoke()

        return DefaultRootComponent(
            componentContext: DefaultComponentContext(
                lifecycle: ApplicationLifecycle(),
                stateKeeper: stateKeeper,
                instanceKeeper: nil,
                backHandler: backDispatcher
            )
        )
    }()

    func application(_: UIApplication, shouldSaveSecureApplicationState coder: NSCoder) -> Bool {
        StateKeeperUtilKt.save(coder: coder, state: stateKeeper.save())
        return true
    }

    func application(_: UIApplication, shouldRestoreSecureApplicationState _: NSCoder) -> Bool {
//        stateKeeper = StateKeeperDispatcherKt.StateKeeperDispatcher(savedState: StateKeeperUtilsKt.restore(coder: coder))
        return true
    }
}
