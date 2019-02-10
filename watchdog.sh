ts=$((`date +%s`-60))
last=`(echo "[0] latency: 0 ms" & (tail -100 logs/snake.log |grep "latency:")) | tail -n 1 | cut -d " " -f1 | tr -d '[\[\]]'`

echo $ts $last
