import React from 'react';

const checkMark = (color) => {
    return (
        <svg className={`w-3 h-3 fill-current text-lime-400 m-2 shrink-0" viewBox="0 0 12 12`}
             xmlns="http://www.w3.org/2000/svg">
            <path d="M10.28 2.28L3.989 8.575 1.695 6.28A1 1 0 00.28 7.695l3 3a1 1 0 001.414 0l7-7A1 1 0 0010.28 2.28z"/>
        </svg>
    )
}

export default function LearningGoals({goals, expectedTime, children}) {
    return (
        <div className="max-w-5xl mx-auto bg-lime-800 flex flex-row mb-8 rounded-lg">
            <div className="w-2 bg-lime-400 rounded-l-lg pl-2"></div>
            <div className="items-center px-6 sm:px-6 py-6">
                <div className="flex flex-row items-center mb-3">
                    <h3 className="h3 mb-0 text-white flex-1">Learning Goals</h3>
                    {expectedTime &&
                        <div className="flex-none text-lg text-white">
                            Expected time: <span className="font-bold">{expectedTime}</span>
                        </div>
                    }
                </div>
                <div className="text-xl text-white mb-4">
                    {children}
                </div>
                <ul className="text-lg text-white mb-2 -ml-2">
                    {goals.map((goal) => {
                        return (
                            <li className="flex items-start mb-2">
                                {checkMark("lime")}
                                <span>{goal}</span>
                            </li>
                        )
                    })}
                </ul>
            </div>
        </div>
    )
}
