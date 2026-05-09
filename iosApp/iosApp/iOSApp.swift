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

        let dismissAction = UNNotificationAction(
            identifier: "ALARM_DISMISS",
            title: "Dismiss",
            options: [.destructive]
        )

        let alarmFiredCategory = UNNotificationCategory(
            identifier: "ALARM_FIRED_CATEGORY",
            actions: [dismissAction],
            intentIdentifiers: [],
            options: []
        )

        let alarmUpcomingCategory = UNNotificationCategory(
            identifier: "ALARM_UPCOMING_CATEGORY",
            actions: [dismissAction],
            intentIdentifiers: [],
            options: []
        )

        center.setNotificationCategories([timerCategory, alarmFiredCategory, alarmUpcomingCategory])

        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Notification authorization error: \(error)")
            }
        }

        return true
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let categoryIdentifier = response.notification.request.content.categoryIdentifier
        if categoryIdentifier == "TIMER_CATEGORY" {
            IosTimerNotificationActionBridge.shared.handle(actionId: response.actionIdentifier)
        } else if categoryIdentifier == "ALARM_FIRED_CATEGORY" || categoryIdentifier == "ALARM_UPCOMING_CATEGORY" {
            IosAlarmNotificationActionBridge.shared.handle(
                actionId: response.actionIdentifier,
                userInfo: response.notification.request.content.userInfo
            )
        }
        completionHandler()
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        IosAlarmNotificationActionBridge.shared.checkMissedAlarms()
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .list, .sound])
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
