(function() {
  var style = getComputedStyle(document.documentElement);
  var accent = style.getPropertyValue('--accent').trim();
  var accent2 = style.getPropertyValue('--accent2').trim();
  var ink = style.getPropertyValue('--ink').trim();
  var muted = style.getPropertyValue('--muted').trim();
  var rule = style.getPropertyValue('--rule').trim();
  var bg2 = style.getPropertyValue('--bg2').trim();

  // --- Chart: coverage comparison ---
  var chartCoverage = echarts.init(document.getElementById('chart-coverage'), null, { renderer: 'svg' });
  chartCoverage.setOption({
    animation: false,
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, appendToBody: true },
    legend: { data: ['LINE', 'BRANCH', 'INSTRUCTION'], bottom: 0, textStyle: { color: ink } },
    grid: { left: '3%', right: '4%', bottom: '12%', top: '10%', containLabel: true },
    xAxis: { type: 'category', data: ['CarbonAccountingController', 'CarbonAccountingServiceImpl'], axisLine: { lineStyle: { color: rule } }, axisLabel: { color: ink, interval: 0 } },
    yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%', color: muted }, splitLine: { lineStyle: { color: rule } } },
    series: [
      { name: 'LINE', type: 'bar', data: [99, 100], itemStyle: { color: accent }, barWidth: '22%' },
      { name: 'BRANCH', type: 'bar', data: [88, 100], itemStyle: { color: accent2 }, barWidth: '22%' },
      { name: 'INSTRUCTION', type: 'bar', data: [99, 100], itemStyle: { color: muted }, barWidth: '22%' }
    ]
  });
  window.addEventListener('resize', function() { chartCoverage.resize(); });
})();
