import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView(viewModel: ContentView.ViewModel())
                .environmentObject(ContentView.ViewModel())
        }
    }
}
