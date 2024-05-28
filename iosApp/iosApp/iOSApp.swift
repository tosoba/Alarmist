import SwiftUI
import ComposeApp

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
        PlatformKoinInitializer.init().invoke()
        
        return DefaultRootComponent(
            componentContext: DefaultComponentContext(
                lifecycle: ApplicationLifecycle(),
                stateKeeper: stateKeeper,
                instanceKeeper: nil,
                backHandler: backDispatcher
            )
        )
    }()

    func application(_ application: UIApplication, shouldSaveSecureApplicationState coder: NSCoder) -> Bool {
        StateKeeperUtilKt.save(coder: coder, state: stateKeeper.save())
        return true
    }
    
    func application(_ application: UIApplication, shouldRestoreSecureApplicationState coder: NSCoder) -> Bool {
//        stateKeeper = StateKeeperDispatcherKt.StateKeeperDispatcher(savedState: StateKeeperUtilsKt.restore(coder: coder))
        return true
    }
}

