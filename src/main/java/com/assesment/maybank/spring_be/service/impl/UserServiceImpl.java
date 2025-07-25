package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.CountryDto;
import com.assesment.maybank.spring_be.dto.ExchangeRateDto;
import com.assesment.maybank.spring_be.dto.RegisterRequest;
import com.assesment.maybank.spring_be.dto.UserDto;
import com.assesment.maybank.spring_be.dto.UserSummaryDto;
import com.assesment.maybank.spring_be.dto.WeatherDto;
import com.assesment.maybank.spring_be.entity.User;
import com.assesment.maybank.spring_be.enums.CountryCode;
import com.assesment.maybank.spring_be.exception.UserNotFoundException;
import com.assesment.maybank.spring_be.repository.FollowRepository;
import com.assesment.maybank.spring_be.repository.UserRepository;
import com.assesment.maybank.spring_be.service.CountryService;
import com.assesment.maybank.spring_be.service.ExchangeRateService;
import com.assesment.maybank.spring_be.service.UserService;
import com.assesment.maybank.spring_be.service.WeatherService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final CountryService countryService;
    private final WeatherService weatherService;
    private final ExchangeRateService exchangeRateService;

    @Override
    public UserDto createUser(RegisterRequest request) {

        CountryCode countryCode = CountryCode.fromCode(request.getCountryCode());

        User user = User.builder().username(request.getUsername()).countryCode(countryCode.getCode()).build();
        userRepository.save(user);

        int followerCount = 0;
        int followingCount = 0;
        return toDto(user, followerCount, followingCount);
    }

    @Override
    public UserDto getUserById(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        int followerCount = followRepository.countFollowersByFolloweeId(userId);
        int followingCount = followRepository.countFolloweesByFollowerId(userId);

        return toDto(user, followerCount, followingCount);
    }

    private UserDto toDto(User user, int followerCount, int followingCount) {
        return new UserDto(user.getId(), user.getUsername(), followerCount, followingCount);
    }

    @Override
    public UserSummaryDto getUserSummary(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        CountryCode countryCode = CountryCode.fromCode(user.getCountryCode());

        if (countryCode.equals(CountryCode.UNSUPPORTED)) {
            return new UserSummaryDto(null, null, null);
        }

        CountryDto country = countryService.getCountryByCode(countryCode.getCode());

        double lat = country.latitude();
        double lon = country.longitude();

        WeatherDto weather = weatherService.getWeatherByLatitudeLongitude(lat, lon);
        ExchangeRateDto exchangeRate = exchangeRateService.getExchangeRateToUsdByCountry(countryCode.getCurrencyCode());
        return new UserSummaryDto(country, weather, exchangeRate);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        int followerCount = followRepository.countFollowersByFolloweeId(user.getId());
        int followingCount = followRepository.countFolloweesByFollowerId(user.getId());

        return toDto(user, followerCount, followingCount);
    }

}
