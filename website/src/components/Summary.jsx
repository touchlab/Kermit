import React from 'react';

export default function Summary() {
  return (
    <section>
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
          {/* Section header */}
          <div className="max-w-4xl mx-auto text-center pb-12 md:pb-20">
            <h2 className="h2 mb-4">PlasmaUI is a fully native cross-platform UI</h2>
            <div className="text-xl text-gray-700 dark:text-gray-400">All UI on both platforms is fully native. Compose UI on Android and Swift UI on iOS. PlasmaUI is a Compose DSL, with
            a familiar subset of Jetpack Compose UI, that can be rendered by Swift UI components on iOS. It can freely mix, include, or be included by, native UI definitions.</div>
          </div>

      </div>
    </section>
  );
}
