import SwiftUI
import Shared
import Combine

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel
    @State private var selectedTab: Tab = .remote

    enum Tab {
        case remote, local
    }

    var body: some View {
        NavigationView {
            VStack {
                Picker("Data Source", selection: $selectedTab) {
                    Text("Remote").tag(Tab.remote)
                    Text("Local").tag(Tab.local)
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding()

                ListView(phrases: selectedTab == .remote ?
                    viewModel.remoteRocketLaunches :
                    viewModel.localRocketLaunches,
                    showButton: selectedTab == .remote
                )
            }
            .task {
                await self.viewModel.fetchRocketRaunch()
                await self.viewModel.loadLocalRocketLaunches()
            }
            .navigationTitle("Rocket Launches")
        }
    }
}

extension ContentView {
    @MainActor
    class ViewModel: ObservableObject {
        @Published var greetings: [String] = []
        @Published var remoteRocketLaunches: [RocketLaunch] = []
        @Published var localRocketLaunches: [RocketLaunch] = []
        private let rocketLaunchLocalRepo = RocketLaunchLocalRepository()
        private var cancellables: Set<AnyCancellable> = []
        
        func startObserving() async {
            for await phrase in Greeting().greet() {
                self.greetings.append(phrase)
            }
        }
        
        func fetchRocketRaunch() async {
            do {
                let rockets: [RocketLaunch] = try await RocketComponent().fetchLaunch()
                remoteRocketLaunches = rockets
            } catch {
                print(error)
            }
        }
        
        func loadLocalRocketLaunches() async {
//            let publisher = rocketLaunchLocalRepo.rocketLaunchListAsFlow.toPublisher()
//            publisher
//                .receive(on: DispatchQueue.main)
//                .sink(receiveValue: { list in
//                    self.localRocketLaunches = list
//                })
//                .store(in: &cancellables)
            
            for await launches in rocketLaunchLocalRepo.rocketLaunchListAsFlow {
                self.localRocketLaunches = launches
            }
        }
        
        func insertRocketLaunch(_ launch: RocketLaunch) {
            rocketLaunchLocalRepo.insertRocketLaunch(rocketLaunch: launch)
        }
    }
}

struct ListView: View {
    let phrases: Array<RocketLaunch>
    @State var showButton: Bool
    @State private var tappedFlight: Int32?
    private let rocketLaunchLocalRepo = RocketLaunchLocalRepository()
    @EnvironmentObject var viewModel: ContentView.ViewModel

    var body: some View {
        List(phrases, id: \.self) { launch in
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Flight #\(launch.flightNumber)")
                        .font(.headline)
                    Text(launch.missionName)
                        .font(.subheadline)
                    Text(launchDateFormatted(from: launch.launchDateUTC))
                        .font(.caption)
                        .foregroundColor(.gray)
                }
                Spacer()

                if showButton {
                    Button(action: {
                        tappedFlight = launch.flightNumber
                        withAnimation(.easeInOut(duration: 0.2)) {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                                tappedFlight = nil
                            }
                        }
                        viewModel.insertRocketLaunch(launch)
                        print("Add tapped for flight #\(launch.flightNumber)")
                    }) {
                        Image(systemName: "plus.circle.fill")
                            .imageScale(.large)
                            .scaleEffect(tappedFlight == launch.flightNumber ? 1.5 : 1.0)
                            .foregroundColor(.blue)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
            .padding(.vertical, 8)
        }
    }
}

/// UTC ISO8601 → yyyy/MM/dd HH:mm の表示に変換
private func launchDateFormatted(from utcString: String) -> String {
    let formatter = ISO8601DateFormatter()
    guard let date = formatter.date(from: utcString) else {
        return utcString
    }

    let output = DateFormatter()
    output.dateFormat = "yyyy/MM/dd HH:mm"
    return output.string(from: date)
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        let sampleLaunches = [
            RocketLaunch(flightNumber: 161, missionName: "Starlink 4-17 (v1.5)", launchDateUTC: "2022-05-06T09:42:00.000Z", launchSuccess: true),
            RocketLaunch(flightNumber: 162, missionName: "CRS-25", launchDateUTC: "2022-07-14T16:44:00.000Z", launchSuccess: true)
        ]
        ListView(phrases: sampleLaunches, showButton: true)
    }
}

