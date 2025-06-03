package com.saktas.couponservice;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.saktas.couponservice.models.Coupon;
import com.saktas.couponservice.repositories.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) /* Bu testleri koşmak için uygulamayı random port'da servis et demek */
@PactFolder("pacts") /* Contract dosyasını içermesi gereke path */
@Provider("CouponServiceProvider") /* Consumer tarafında provider'e verien isim bknz(PactConsumerTest.class) */
public class PactProviderTest {

    /**
     * SpringBootTest.WebEnvironment.RANDOM_PORT
     * Uygulamanın test edilebilmesi için ayağa kalktığı port bilgisi local değişkene bind ediyoruz
     **/
    @LocalServerPort
    private int port;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    public void setUpAppInfo(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @State(value = "coupon exist", action = StateChangeAction.SETUP)
    public void couponExistBehaviourSetup(Map<String, Object> params) {
        String couponCode = params.get("code").toString();
        Optional<Coupon> coupon = couponRepository.findByCode(couponCode);
        if (coupon.isPresent()) {
            System.out.println(couponCode + " is already present in db");
        }else{
            Coupon newCoupon = new Coupon();
            newCoupon.setCode(couponCode);
            newCoupon.setDiscount(BigDecimal.valueOf(1100));
            newCoupon.setExpDate("12/20/2030");
            couponRepository.save(newCoupon);
        }
    }

    @State(value = "coupon exist", action = StateChangeAction.TEARDOWN)
    public void couponExistBehaviourTearDown(Map<String, Object> params) {
        String couponCode = params.get("code").toString();
        Optional<Coupon> coupon = couponRepository.findByCode(couponCode);
        if (coupon.isPresent()) {
            System.out.println(couponCode + " is already present in db");
        }else{
            Coupon newCoupon = new Coupon();
            newCoupon.setCode(couponCode);
            newCoupon.setDiscount(BigDecimal.valueOf(1100));
            newCoupon.setExpDate("12/20/2030");
            couponRepository.save(newCoupon);
        }
    }

    @State(value = "coupon not found", action = StateChangeAction.SETUP)
    public void couponNotExistBehaviourSetup(Map<String, Object> params) {
        String couponCode = params.get("code").toString();
        Optional<Coupon> coupon = couponRepository.findByCode(couponCode);
        coupon.ifPresent(value -> couponRepository.delete(value));
    }

    @State(value = "coupon not found", action = StateChangeAction.TEARDOWN)
    public void couponNotExistBehaviourTearDown(Map<String, Object> params) {
        String couponCode = params.get("code").toString();
        Optional<Coupon> coupon = couponRepository.findByCode(couponCode);
        if (coupon.isEmpty()){
            Coupon newCoupon = new Coupon();
            newCoupon.setCode(couponCode);
            newCoupon.setDiscount(BigDecimal.valueOf(1100));
            newCoupon.setExpDate("12/20/2030");
            couponRepository.save(newCoupon);
        }
    }

    @TestTemplate // DataProvider for junit5
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void pactVerificationTest(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
