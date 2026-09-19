"use client";

type Props = {
  title: string;
  data: { label: string; value: number }[];
  currency?: string;
};

export function FinanceAgeingChart({ title, data, currency = "" }: Props) {
  const max = Math.max(...data.map(x => x.value), 1);
  const chartHeight = 180;
  const baseline = 205;
  const plotTop = 20;
  const barWidth = 58;
  const gap = 30;
  const width = data.length * (barWidth + gap) + gap;

  return (
    <section className="card finance-chart-card">
      <div className="finance-chart-head">
        <div>
          <div className="finance-chart-title">{title}</div>
          <div className="finance-chart-subtitle">Outstanding balance by ageing bucket</div>
        </div>
      </div>
      <div className="finance-chart-scroll">
        <svg className="finance-chart" viewBox={`0 0 ${width} 250`} role="img" aria-label={title}>
          <line x1="20" y1={baseline} x2={width - 20} y2={baseline} className="finance-chart-axis" />
          {data.map((item, index) => {
            const x = gap + index * (barWidth + gap);
            const height = item.value > 0 ? Math.max(4, (item.value / max) * chartHeight) : 2;
            const y = baseline - height;
            return (
              <g key={item.label}>
                <rect x={x} y={y} width={barWidth} height={height} rx="4" className="finance-chart-bar" />
                <text x={x + barWidth / 2} y={Math.max(plotTop + 12, y - 8)} textAnchor="middle" className="finance-chart-value">
                  {currency}{item.value.toFixed(2)}
                </text>
                <text x={x + barWidth / 2} y="230" textAnchor="middle" className="finance-chart-label">
                  {item.label}
                </text>
              </g>
            );
          })}
        </svg>
      </div>
    </section>
  );
}
