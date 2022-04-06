import Foundation
import shared

func startKoin() {
    _koin = KoinDependencyHelper()
}

private var _koin: KoinDependencyHelper?
var koinHelper: KoinDependencyHelper {
    return _koin!
}
