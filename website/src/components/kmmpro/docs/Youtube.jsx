import React from 'react';

export default function Youtube({videoUrl, videoKey}) {
    const videoUrlResolved = videoKey ? `https://www.youtube.com/embed/${videoKey}` : videoUrl
    return (
        <section>
            <iframe className="w-full aspect-video" src={videoUrlResolved} frameBorder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowFullScreen></iframe>
        </section>
    );
}
