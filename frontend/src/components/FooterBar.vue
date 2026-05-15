<template>
  <footer class="footer">
    <div class="container footer-top">
      <section class="footer-brand">
        <span class="footer-mark">茶</span>
        <div>
          <span class="footer-label">YUNMING TEA HOUSE</span>
          <h4>云茗茶馆</h4>
          <p>以茶会友，以器载道，选茶、待客、雅集</p>
        </div>
      </section>

      <section class="footer-column">
        <span class="footer-label">馆内服务</span>
        <h5>经营入口</h5>
        <p>茶品选购</p>
        <p>包间雅集</p>
        <p>节令茶会</p>
      </section>

      <section class="footer-column">
        <span class="footer-label">联系信息</span>
        <h5>到店联络</h5>
        <p>杭州市西湖区龙井路 88 号</p>
        <p>0571-8888-6666</p>
        <p>服务时间 09:00 - 21:00</p>
      </section>
    </div>

    <div class="container footer-bottom">
      <span>YUNMING TEA HOUSE</span>
      <span>© 2026 云茗茶馆</span>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  margin-top: 70px;
  color: rgba(250, 243, 233, 0.84);
  background:
    linear-gradient(180deg, rgba(89, 62, 49, 0.98), rgba(60, 42, 33, 0.98));
}

.footer-top {
  display: grid;
  grid-template-columns: 1.35fr 1fr 1fr;
  gap: 28px;
  padding-top: 42px;
  padding-bottom: 20px;
}

.footer-brand {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.footer-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border: 1px solid rgba(245, 224, 188, 0.32);
  color: #f6e7c9;
  font-family: var(--font-display);
  font-size: 17px;
  letter-spacing: 0.16em;
}

.footer-label {
  display: inline-block;
  color: rgba(246, 231, 201, 0.62);
  font-size: 10px;
  letter-spacing: 0.22em;
  text-transform: uppercase;
}

.footer-brand h4,
.footer-column h5 {
  margin: 8px 0 10px;
  color: #f5e6c8;
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.footer-brand p,
.footer-column p {
  margin: 0 0 6px;
  color: rgba(250, 243, 233, 0.7);
  font-size: 13px;
  line-height: 1.8;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding-top: 14px;
  padding-bottom: 16px;
  border-top: 1px solid rgba(245, 224, 188, 0.12);
  color: rgba(246, 231, 201, 0.56);
  font-size: 12px;
  letter-spacing: 0.16em;
}
</style>
