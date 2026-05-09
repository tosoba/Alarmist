import ComposeApp
import SwiftUI
import UserNotifications

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    var body: some Scene {
        WindowGroup {
            RootView(component: appDelegate.component, backDispatcher: appDelegate.backDispatcher)
                .ignoresSafeArea()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    private var stateKeeper = StateKeeperDispatcherKt.StateKeeperDispatcher(savedState: nil)
    let backDispatcher: BackDispatcher = BackDispatcherKt.BackDispatcher()

    lazy var component: RootComponent = {
        LogConfigKt.doInitNapierDebug()
        PlatformKoinInitializer().invoke(additionalModules: [])

        return DefaultRootComponent(
            componentContext: DefaultComponentContext(
                lifecycle: ApplicationLifecycle(),
                stateKeeper: stateKeeper,
                instanceKeeper: nil,
                backHandler: backDispatcher
            )
        )
    }()

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        let center = UNUserNotificationCenter.current()
        center.delegate = self

        let cancelAction = UNNotificationAction(
            identifier: "TIMER_CANCEL",
            title: "Cancel",
            options: [.destructive]
        )

        let timerCategory = UNNotificationCategory(
            identifier: "TIMER_CATEGORY",
            actions: [cancelAction],
            intentIdentifiers: [],
            options: []
        )

        center.setNotificationCategories([timerCategory])
        return true
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        IosTimerNotificationActionBridge.shared.handle(actionId: response.actionIdentifier)
        completionHandler()
    }

    func application(_: UIApplication, shouldSaveSecureApplicationState coder: NSCoder) -> Bool {
        StateKeeperUtilKt.save(coder: coder, state: stateKeeper.save())
        return true
    }

    func application(_: UIApplication, shouldRestoreSecureApplicationState coder: NSCoder) -> Bool {
        stateKeeper = StateKeeperDispatcherKt.StateKeeperDispatcher(savedState: StateKeeperUtilKt.restore(coder: coder))
        return true
    }
}
