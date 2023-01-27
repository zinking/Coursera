pub fn radix_sort(arr: &mut [u64]) {
    let len = arr.len();
    if len < 2 {
        return;
    }

    let max: usize = *(arr.iter().max().unwrap()) as usize;
    let radix = len.next_power_of_two();
    let mut place = 1;
    println!("radix {radix}");

    while place <= max {
        let digit_of = |x: u64| x as usize / place % radix;
        let mut counter = vec![0; radix];
        for &x in arr.iter() {
            counter[digit_of(x)] += 1;
        }

        for i in 1..radix {
            counter[i] += counter[i - 1];
        }

        for &x in arr.to_owned().iter().rev() {
            counter[digit_of(x)] -= 1;
            arr[counter[digit_of(x)]] = x;
        }
        place *= radix;
    }

}

#[cfg(test)]
mod test {
    use crate::sort::radix_sort::radix_sort;

    #[test]
    fn test_empty_vec() {
        let mut arr: Vec<u64> = vec![];
        radix_sort(&mut arr);
        assert_eq!(arr, vec![])
    }

    #[test]
    fn test_number_vec() {
        let mut arr = vec![3, 1, 5, 7];
        radix_sort(&mut arr);
        assert_eq!(arr, vec![1, 3, 5, 7])
    }

    #[test]
    fn test_number_vec2() {
        let mut arr = vec![7, 49, 73, 58, 30, 72, 44, 78, 23, 9];
        radix_sort(&mut arr);
        assert_eq!(arr, vec![7, 9, 23, 30, 44, 49, 58, 72, 73, 78]);
    }

}