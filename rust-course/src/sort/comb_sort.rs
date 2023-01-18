
pub fn comb_sort<T: Ord>(arr: &mut [T]) {
    let len = arr.len();
    if len < 2 {
        return;
    }

    let mut gap = len;
    let mut sorted = false;
    let shrink = 1.3;

    while !sorted {
        gap = (gap as f32 / shrink) as usize;

        if gap <= 1 {
            gap = 1;
            sorted = true;
        }

        for i in 0..(len - gap) {
            let j = i + gap;
            if arr[i] > arr[j] {
                arr.swap(i, j);
                sorted = false;
            }
        }
    }

}

#[cfg(test)]
mod tests {
    use crate::sort::comb_sort::comb_sort;

    #[test]
    fn descending() {
        let mut arr = vec![6, 5, 4, 3, 2, 1];
        comb_sort(&mut arr);
        assert_eq!(arr, vec![1, 2, 3, 4, 5, 6]);
    }

    #[test]
    fn ascending() {
        let mut arr = vec![1, 2, 3, 4, 5, 6];
        comb_sort(&mut arr);
        assert_eq!(arr, vec![1, 2, 3, 4, 5, 6]);
    }
}