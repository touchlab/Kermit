import React from 'react';
import { Link } from 'react-router-dom';

String.prototype.hashCode = function() {
  let hash = 0,
      i, chr;
  if (this.length === 0) return hash;
  for (i = 0; i < this.length; i++) {
    chr = this.charCodeAt(i);
    hash = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return hash;
}

const RESERVED_TAGS = {
  "team-process": "bg-rose-600 hover:bg-rose-700",
  "kotlin-multiplatform":"bg-emerald-600 hover:bg-emerald-700",
  ios: "bg-blue-600 hover:bg-blue-700",
  ui: "bg-blue-600 hover:bg-blue-700",
  industry: "bg-emerald-600 hover:bg-emerald-700"
}
const TAG_COLORS = [
  "bg-fuchsia-600 hover:bg-fuchsia-700",
  "bg-green-600 hover:bg-green-700",
  "bg-red-600 hover:bg-red-700",
  "bg-orange-600 hover:bg-orange-700",
  "bg-amber-600 hover:bg-amber-700",
  "bg-lime-600 hover:bg-lime-700",
  "bg-teal-600 hover:bg-teal-700",
  "bg-cyan-600 hover:bg-cyan-700",
]

export default function BlogDocTag({tag, showCount}) {
  const reservedColor = RESERVED_TAGS[tag.label]
  const labelColorIndex = Math.abs(tag.label.hashCode()) % TAG_COLORS.length
  const color = reservedColor ? reservedColor : TAG_COLORS[labelColorIndex]
  return (
      <li className="m-1" key={tag.permalink} style={{listStyleType:"none"}}>
        <Link to={tag.permalink} className={`inline-flex text-center text-gray-100 hover:text-white py-1 px-3 rounded-full ${color} transition duration-150 ease-in-out`}>
          {tag.label}
          {showCount && <>&nbsp;<span className="font-bold">{tag.count}</span></>}
        </Link>
      </li>
  )
}
