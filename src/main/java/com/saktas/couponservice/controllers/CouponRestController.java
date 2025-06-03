package com.saktas.couponservice.controllers;

import com.saktas.couponservice.models.Coupon;
import com.saktas.couponservice.repositories.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/couponapi")
public class CouponRestController {

    @Autowired
    private CouponRepository couponRepository;

    @RequestMapping(value = "/coupons", method = RequestMethod.POST)
    public Coupon create(@RequestBody Coupon coupon){
        return couponRepository.save(coupon);
    }

    @RequestMapping(value = "/coupons/{couponCode}", method = RequestMethod.GET)
    public ResponseEntity<Coupon> getCoupon(@PathVariable("couponCode") String code) {
        Optional<Coupon> couponOptional = couponRepository.findByCode(code);

        // Returns 404 with an empty body
        // Or if you want a custom error message/object:
        // return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Returns 404 with null body
        // return new ResponseEntity<>(new ErrorResponse("Coupon not found"), HttpStatus.NOT_FOUND); // Custom error object
        return couponOptional.map(coupon -> new ResponseEntity<>(coupon, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
