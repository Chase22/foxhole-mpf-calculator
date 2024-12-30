import {describe, expect, test} from '@jest/globals';
import {calculateQueueCost} from "./Cost";

describe('calculateCost', () => {
    test("returns 0 when no cost are given", () => {
        expect(calculateQueueCost(0, 0)).toBe(0)
    })

    test("gives a 10% discount if 1 crate is requested", () => {
        expect(calculateQueueCost(100, 1)).toBe(90)
    })

    test("gives a 30% discount if 2 crates are requested", () => {
        expect(calculateQueueCost(100, 2)).toBe(170)
    })

    test("gives a 50% discount at 5 crates", () => {
        expect(calculateQueueCost(100, 5)).toBe(350)
    })

    test("gives a 50% discount at 9 crates", () => {
        expect(calculateQueueCost(100, 9)).toBe(550)
    })
});

