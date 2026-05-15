<template>
  <footer class="footer">
    <div class="container footer-top">
      <section class="footer-brand">
        <span class="footer-mark">茶</span>
        <div class="footer-brand__text">
          <span class="footer-label">YUNMING TEA HOUSE</span>
          <h4>云茗茶馆</h4>
          <p class="footer-brand__tagline">一盏茶，安放片刻清欢</p>
          <div class="footer-brand__actions">
            <router-link to="/products">浏览茶品</router-link>
            <router-link to="/rooms">预约雅间</router-link>
          </div>
        </div>
      </section>

      <section class="footer-column">
        <span class="footer-label">站内导航</span>
        <h5>逛馆入口</h5>
        <router-link to="/products">全部茶品</router-link>
        <router-link to="/categories">茶品分类</router-link>
        <router-link to="/rooms">包间预约</router-link>
        <router-link to="/activities">茶事活动</router-link>
      </section>

      <section class="footer-column">
        <span class="footer-label">联系信息</span>
        <h5>到店联络</h5>
        <p>杭州市西湖区龙井路 88 号</p>
        <p><a href="tel:0571-8888-6666">0571-8888-6666</a></p>
        <p>服务时间 09:00 - 21:00</p>
        <p class="footer-contact__note">建议提前预约，到店体验更从容</p>
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
  margin-top: 52px;
  color: rgba(242, 233, 219, 0.8);
  background:
    linear-gradient(180deg,
      rgba(82, 60, 47, 0.97) 0%,
      rgba(72, 52, 41, 0.98) 52%,
      rgba(61, 45, 36, 0.99) 100%);
}

.footer-top {
  display: grid;
  grid-template-columns: 1.15fr 0.9fr 1fr;
  gap: 32px;
  padding-top: 26px;
  padding-bottom: 18px;
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
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border: 1px solid rgba(221, 197, 154, 0.28);
  color: #ead6af;
  font-family: var(--font-display);
  font-size: 18px;
  letter-spacing: 0.08em;
}

.footer-brand__text {
  flex: 1;
}

.footer-brand__tagline {
  margin: 0;
  color: rgba(239, 227, 208, 0.68);
  font-size: 12px;
  line-height: 1.7;
  letter-spacing: 0.04em;
}

.footer-brand__actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-top: 12px;
}

.footer-brand__actions a {
  display: inline-flex;
  align-items: center;
  color: #829985;
  font-size: 13px;
  letter-spacing: 0.08em;
  transition:
    color var(--transition-base),
    transform var(--transition-base);
}

.footer-brand__actions a:hover {
  color: #a7baaa;
  transform: translateX(2px);
}

.footer-label {
  display: inline-block;
  color: rgba(221, 197, 154, 0.68);
  font-size: 10px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.footer-brand h4,
.footer-column h5 {
  margin: 8px 0 6px;
  color: #eddab7;
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.footer-column h5 {
  font-size: 14px;
  letter-spacing: 0.08em;
}

.footer-column p {
  margin: 0 0 5px;
  color: rgba(239, 227, 208, 0.72);
  font-size: 13px;
  line-height: 1.7;
}

.footer-column a {
  display: block;
  margin-bottom: 2px;
  padding: 4px 0 4px 12px;
  color: rgba(239, 227, 208, 0.68);
  font-size: 13px;
  line-height: 1.7;
  position: relative;
  transition:
    color var(--transition-base),
    padding-left var(--transition-base);
}

.footer-column a::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: rgba(130, 153, 133, 0.58);
  transition:
    background var(--transition-base),
    width var(--transition-base),
    height var(--transition-base);
}

.footer-column a:hover {
  color: #a7baaa;
  padding-left: 15px;
}

.footer-column a:hover::before {
  background: #a7baaa;
  width: 2px;
  height: 10px;
  border-radius: 2px;
}

.footer-contact__note {
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid rgba(221, 197, 154, 0.1);
  color: rgba(221, 197, 154, 0.54) !important;
  font-size: 12px !important;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding-top: 10px;
  padding-bottom: 12px;
  border-top: 1px solid rgba(221, 197, 154, 0.1);
  color: rgba(221, 197, 154, 0.58);
  font-size: 12px;
  letter-spacing: 0.14em;
}

/* Tablet: two columns */
@media (max-width: 960px) {
  .footer-top {
    grid-template-columns: 1fr 1fr;
    gap: 26px;
    padding-top: 24px;
    padding-bottom: 18px;
  }

  .footer-brand {
    grid-column: 1 / -1;
  }
}

/* Mobile: single column */
@media (max-width: 640px) {
  .footer-top {
    grid-template-columns: 1fr;
    gap: 22px;
    padding-top: 22px;
    padding-bottom: 16px;
  }

  .footer-brand {
    grid-column: auto;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .footer-brand__actions {
    justify-content: center;
  }

  .footer-column {
    text-align: center;
  }

  .footer-column a {
    padding-left: 0;
  }

  .footer-column a::before {
    display: none;
  }

  .footer-column a:hover {
    padding-left: 0;
  }

  .footer-bottom {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>
