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
import com.assesment.maybank.spring_be.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private CountryService countryService;

    @Mock
    private WeatherService weatherService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User mockUser;
    private RegisterRequest registerRequest;
    private CountryDto mockCountry;
    private WeatherDto mockWeather;
    private ExchangeRateDto mockExchangeRate;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        mockUser = User.builder()
                .id(userId)
                .username("testuser")
                .password("testpassword")
                .countryCode("MY")
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("newpassword");
        registerRequest.setCountryCode("MY");

        mockCountry = new CountryDto("Malaysia", "Malaysia", Arrays.asList("Kuala Lumpur"), "MY", 3.1390, 101.6869);

        mockWeather = new WeatherDto(
                new WeatherDto.WeatherMetric(28.5, "°C"),
                new WeatherDto.WeatherMetric(15.2, "km/h"),
                new WeatherDto.WeatherMetric(180.0, "°"));

        mockExchangeRate = new ExchangeRateDto("MYR", "USD", 0.23);
    }

    @Test
    void createUser_ShouldReturnUserDto_WhenValidRequest() {

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .password("newpassword")
                .countryCode("MY")
                .build();

        try (MockedStatic<CountryCode> countryCodeMock = mockStatic(CountryCode.class)) {
            CountryCode mockCountryCode = mock(CountryCode.class);
            when(mockCountryCode.getCode()).thenReturn("MY");
            countryCodeMock.when(() -> CountryCode.fromCode("MY")).thenReturn(mockCountryCode);

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserDto result = userService.createUser(registerRequest);

            assertNotNull(result);
            assertEquals(savedUser.getId(), result.id());
            assertEquals("newuser", result.username());
            assertEquals(0, result.followerCount());
            assertEquals(0, result.followingCount());

            verify(userRepository).save(argThat(user -> user.getUsername().equals("newuser") &&
                    user.getPassword().equals("newpassword") &&
                    user.getCountryCode().equals("MY")));
            countryCodeMock.verify(() -> CountryCode.fromCode("MY"));
        }
    }

    @Test
    void createUser_ShouldHandleDifferentCountryCodes() {

        registerRequest.setCountryCode("US");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .password("newpassword")
                .countryCode("US")
                .build();

        try (MockedStatic<CountryCode> countryCodeMock = mockStatic(CountryCode.class)) {
            CountryCode mockCountryCode = mock(CountryCode.class);
            when(mockCountryCode.getCode()).thenReturn("US");
            countryCodeMock.when(() -> CountryCode.fromCode("US")).thenReturn(mockCountryCode);

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserDto result = userService.createUser(registerRequest);

            assertNotNull(result);
            assertEquals("US", savedUser.getCountryCode());
            countryCodeMock.verify(() -> CountryCode.fromCode("US"));
        }
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(followRepository.countFollowersByFolloweeId(userId)).thenReturn(5);
        when(followRepository.countFolloweesByFollowerId(userId)).thenReturn(3);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("testuser", result.username());
        assertEquals(5, result.followerCount());
        assertEquals(3, result.followingCount());

        verify(userRepository).findById(userId);
        verify(followRepository).countFollowersByFolloweeId(userId);
        verify(followRepository).countFolloweesByFollowerId(userId);
    }

    @Test
    void getUserById_ShouldThrowUserNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));

        verify(userRepository).findById(userId);
        verify(followRepository, never()).countFollowersByFolloweeId(any());
        verify(followRepository, never()).countFolloweesByFollowerId(any());
    }

    @Test
    void getUserById_ShouldHandleZeroFollowCounts() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(followRepository.countFollowersByFolloweeId(userId)).thenReturn(0);
        when(followRepository.countFolloweesByFollowerId(userId)).thenReturn(0);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(0, result.followerCount());
        assertEquals(0, result.followingCount());
    }

    @Test
    void getUserSummary_ShouldReturnUserSummaryDto_WhenSupportedCountry() {

        try (MockedStatic<CountryCode> countryCodeMock = mockStatic(CountryCode.class)) {
            CountryCode mockCountryCode = mock(CountryCode.class);

            when(mockCountryCode.getCode()).thenReturn("MY");
            when(mockCountryCode.getCurrencyCode()).thenReturn("MYR");

            countryCodeMock.when(() -> CountryCode.fromCode("MY")).thenReturn(mockCountryCode);

            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(countryService.getCountryByCode("MY")).thenReturn(mockCountry);
            when(weatherService.getWeatherByLatitudeLongitude(3.1390, 101.6869)).thenReturn(mockWeather);
            when(exchangeRateService.getExchangeRateToUsdByCountry("MYR")).thenReturn(mockExchangeRate);

            UserSummaryDto result = userService.getUserSummary(userId);

            assertNotNull(result);
            assertEquals(mockCountry, result.country());
            assertEquals(mockWeather, result.weather());
            assertEquals(mockExchangeRate, result.exchangeRate());

            verify(userRepository).findById(userId);
            verify(countryService).getCountryByCode("MY");
            verify(weatherService).getWeatherByLatitudeLongitude(3.1390, 101.6869);
            verify(exchangeRateService).getExchangeRateToUsdByCountry("MYR");
        }
    }

    @Test
    void getUserSummary_ShouldThrowUserNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserSummary(userId));

        verify(userRepository).findById(userId);
        verify(countryService, never()).getCountryByCode(any());
    }

    @Test
    void getUserEntityByUsername_ShouldReturnUser_WhenUserExists() {

        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserEntityByUsername(username);

        assertNotNull(result);
        assertEquals(mockUser, result);
        assertEquals(username, result.getUsername());

        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserEntityByUsername_ShouldThrowUserNotFoundException_WhenUserNotFound() {

        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserEntityByUsername(username));

        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserEntityById_ShouldReturnUser_WhenUserExists() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserEntityById(userId);

        assertNotNull(result);
        assertEquals(mockUser, result);
        assertEquals(userId, result.getId());

        verify(userRepository).findById(userId);
    }

    @Test
    void getUserEntityById_ShouldThrowUserNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserEntityById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void getUserSummary_ShouldUseCorrectCoordinates_ForWeatherService() {

        CountryDto customCountry = new CountryDto("Singapore", "Republic of Singapore",
                Arrays.asList("Singapore"), "SG", 1.3521, 103.8198);

        try (MockedStatic<CountryCode> countryCodeMock = mockStatic(CountryCode.class)) {
            CountryCode mockCountryCode = mock(CountryCode.class);

            when(mockCountryCode.getCode()).thenReturn("SG");
            when(mockCountryCode.getCurrencyCode()).thenReturn("SGD");

            countryCodeMock.when(() -> CountryCode.fromCode("MY")).thenReturn(mockCountryCode);

            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(countryService.getCountryByCode("SG")).thenReturn(customCountry);
            when(weatherService.getWeatherByLatitudeLongitude(1.3521, 103.8198)).thenReturn(mockWeather);
            when(exchangeRateService.getExchangeRateToUsdByCountry("SGD")).thenReturn(mockExchangeRate);

            userService.getUserSummary(userId);

            verify(weatherService).getWeatherByLatitudeLongitude(1.3521, 103.8198);
        }
    }

    @Test
    void toDto_ShouldMapAllFieldsCorrectly() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(followRepository.countFollowersByFolloweeId(userId)).thenReturn(10);
        when(followRepository.countFolloweesByFollowerId(userId)).thenReturn(15);

        UserDto result = userService.getUserById(userId);

        assertEquals(mockUser.getId(), result.id());
        assertEquals(mockUser.getUsername(), result.username());
        assertEquals(10, result.followerCount());
        assertEquals(15, result.followingCount());
    }

    @Test
    void createUser_ShouldAlwaysStartWithZeroFollowCounts() {

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .password("newpassword")
                .countryCode("MY")
                .build();

        try (MockedStatic<CountryCode> countryCodeMock = mockStatic(CountryCode.class)) {
            CountryCode mockCountryCode = mock(CountryCode.class);
            when(mockCountryCode.getCode()).thenReturn("MY");
            countryCodeMock.when(() -> CountryCode.fromCode("MY")).thenReturn(mockCountryCode);

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserDto result = userService.createUser(registerRequest);

            assertEquals(0, result.followerCount());
            assertEquals(0, result.followingCount());

            // Verify follow counts are not queried for new users
            verify(followRepository, never()).countFollowersByFolloweeId(any());
            verify(followRepository, never()).countFolloweesByFollowerId(any());
        }
    }
}