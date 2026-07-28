// A toran is the garland of mango leaves, marigolds and bells that hangs
// above a doorway during Ganeshotsav to welcome Bappa. Drawing it by hand
// (instead of using a stock "festive banner" graphic) is this site's
// signature element - it's the first thing that tells a mandal member
// "this was made for us", not generated from a generic template.

const LEAF_COUNT = 13

function Leaf({ x, rotate }) {
  return (
    <g transform={`translate(${x}, 6) rotate(${rotate})`} className="toran-leaf">
      <path d="M0,0 C -9,10 -9,26 0,34 C 9,26 9,10 0,0 Z" fill="var(--leaf-green)" />
      <path d="M0,2 L0,30" stroke="var(--leaf-vein)" strokeWidth="1" opacity="0.5" />
    </g>
  )
}

function Marigold({ x }) {
  return (
    <g transform={`translate(${x}, 40)`} className="toran-bud">
      <line x1="0" y1="-8" x2="0" y2="0" stroke="var(--rope)" strokeWidth="1.5" />
      <circle r="6.5" fill="var(--marigold)" />
      <circle r="3" fill="var(--marigold-dark)" />
    </g>
  )
}

export default function Toran({ width = 760 }) {
  const spacing = width / (LEAF_COUNT + 1)
  const leaves = Array.from({ length: LEAF_COUNT }, (_, i) => {
    const x = spacing * (i + 1)
    const rotate = i % 2 === 0 ? -8 : 8
    return <Leaf key={`leaf-${i}`} x={x} rotate={rotate} />
  })
  const buds = Array.from({ length: LEAF_COUNT - 1 }, (_, i) => {
    const x = spacing * (i + 1) + spacing / 2
    return <Marigold key={`bud-${i}`} x={x} />
  })

  return (
    <svg className="toran" viewBox={`0 0 ${width} 62`} preserveAspectRatio="none" aria-hidden="true">
      <path
        d={`M0,4 Q${width / 2},34 ${width},4`}
        fill="none"
        stroke="var(--rope)"
        strokeWidth="2.5"
      />
      {leaves}
      {buds}
    </svg>
  )
}
